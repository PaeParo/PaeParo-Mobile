package com.paeparo.paeparo_mobile.fragment

import android.app.AlertDialog
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.activity.PlanGenerateActivity
import com.paeparo.paeparo_mobile.application.getPaeParo
import com.paeparo.paeparo_mobile.databinding.FragmentPlanCompanionBinding
import com.paeparo.paeparo_mobile.databinding.ItemAddCompanionBinding
import com.paeparo.paeparo_mobile.databinding.ItemCompanionInvitedBinding
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import com.paeparo.paeparo_mobile.model.User
import com.paeparo.paeparo_mobile.util.ImageUtil
import kotlinx.coroutines.*
import timber.log.Timber

class PlanCompanionFragment : Fragment() {
    companion object {
        const val DELAY_BEFORE_SEARCH = 5000L
    }

    private var _binding: FragmentPlanCompanionBinding? = null
    private val bindingPlan get() = _binding!!
    private var users = listOf<User>()
    private var job: Job? = null // 단어가 수정될 떄, 기다리는 Job
    private var previousQuery: String? = null // 이전 검색문
    val invitedUserList = mutableListOf<User>() // 초대목록


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanCompanionBinding.inflate(inflater, container, false)
        val activity = activity as PlanGenerateActivity
        bind(activity)
        return bindingPlan.root
    }

    /**
     * Search user
     * 유효성 검사 후, Firebase에 nickName을 통해서 유저를 검색
     * 검사 목록
     * 1. 공백이 존재할 경우, 정규식을 통해 공백 삭제
     * 2. Null 또는 "" 일 경우, cancel
     * 3. 이전 검색과 같은 문장일 경우, cancel
     * 4. 결과가 존재하지 않을 경우, cancel (리사이클러뷰를 만들지 않기 위함)
     * @param userName 사용자 닉네임
     */
    fun searchUser(userName: String?) {
        val filteredName = userName?.replace("\\s+".toRegex(), "") // 공백 제거
        if (filteredName == "") return // 공백의 문자만 존재할 경우
        if (filteredName.equals(previousQuery)) return // 이전 검색과 같을 경우,
        previousQuery = filteredName
        CoroutineScope(Dispatchers.IO).launch {
            val result = FirebaseManager.getUsersStartWith(filteredName!!)
            val data = result.data ?: return@launch             // 결과가 없을 경우,
            withContext(Dispatchers.Main) {
                val mutableList = data.toMutableList()
                val currentUserId = context?.getPaeParo()?.userId
                // 결과에 본인이 포함되어 있을 경우 삭제
                mutableList.removeIf { user: User -> user.userId == currentUserId }
                bindingPlan.rvPlanCompanion.adapter = SearchViewAdapter(mutableList)
            }
        }
    }

    private fun showDialog(activity: PlanGenerateActivity) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("동행자 안내")
            .setMessage("동행자가 설정되지 않았습니다. 다음페이지로 넘어가시겠습니까?")
            .setPositiveButton("확인", { dialog, which ->
                activity.binding.vpPlanGenerate.currentItem++
            })

        val dialog = builder.create()
        dialog.show()
    }

    private fun showCheckDialog(activity: PlanGenerateActivity) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("동행자 추가하기").also {
            if (invitedUserList.size == 0) {
                it.setMessage("동행자가 설정되지 않았습니다. \n다음페이지로 넘어가시겠습니까?")
            } else {
                it.setMessage("총 동행자는 ${invitedUserList.size}명 입니다. \n다음 페이지로 넘어가시겠습니까?")
            }

        }.setPositiveButton("확인") { _, _ ->
            // Trip 객체를 위한 UserID 객체 생성
            val memberList: MutableList<String> = mutableListOf()
            for (user in invitedUserList) {
                memberList.add(user.userId)
            }
            activity.trip.invitedUserList = memberList
            activity.trip.members = listOf(requireContext().getPaeParo().userId)
            activity.binding.vpPlanGenerate.currentItem++
        }.setNegativeButton("취소") { _, _ ->

            return@setNegativeButton
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun bind(activity: PlanGenerateActivity) {
        with(bindingPlan) {
            tvInviteValue.text = invitedUserList.size.toString()
            btnCompanionNext.setOnClickListener {
                showCheckDialog(activity)
            }

            svPlanCompanion.setOnQueryTextListener(SearchUserListener())
            // SearchView 글자 색 바꾸기
            val editText =
                svPlanCompanion.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
            with(editText) {
                setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                setHintTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                setTypeface(null, Typeface.NORMAL) // 이텔릭체 제거
                hint = "닉네임을 입력해주세요."
                // 텍스트 입력 시, 다음 버튼 사라짐.
                setOnFocusChangeListener { v, hasFocus ->
                    Timber.d("focus: $hasFocus")
                    if (hasFocus) {
                        btnCompanionNext.visibility = View.INVISIBLE
                    } else btnCompanionNext.visibility = View.VISIBLE
                }
            }
            // 검색 결과를 보여주는 리사이크러 뷰
            rvPlanCompanion.layoutManager = LinearLayoutManager(context)
            rvPlanCompanion.adapter = SearchViewAdapter(users)

            // 초대된 유저를 보여주는 리사이클러 뷰
            rvPlanCompanionInvited.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            rvPlanCompanionInvited.adapter = InvitedCompanionAdapter()
        }
    }
    /////////////////////////////////////////////////////////////////////////////

    inner class SearchUserListener : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            searchUser(query)
            bindingPlan.btnCompanionNext.visibility = View.VISIBLE
            return false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            if (job != null && job!!.isActive) job!!.cancel()
            job = lifecycleScope.launch(Dispatchers.IO) {
                delay(DELAY_BEFORE_SEARCH)
                searchUser(newText)
            }
            return true
        }
    }

    inner class SearchViewAdapter(private val users: List<User>) :
        RecyclerView.Adapter<SearchViewAdapter.SearchViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
            val binding =
                ItemAddCompanionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return SearchViewHolder(
                binding
            ).also { holder ->
                binding.btnAddCompanion.setOnClickListener {
                    // "초대하기" 버튼을 눌렀을 때,
                    val user = users[holder.layoutPosition]
                    // 초대하지 않은 유저일 경우,
                    if (!invitedUserList.contains(user)) {

                        invitedUserList.add(user) // 리스트에 추가
                        Toast.makeText(context, "${user.nickname}님을 초대하였습니다.", Toast.LENGTH_SHORT)
                            .show()

                        // 초대된 유저 목록 리사이클러뷰 업데이트
                        notifyDataSetChanged()
                        bindingPlan.rvPlanCompanionInvited.adapter?.notifyDataSetChanged()
                        if (invitedUserList.size != 0) bindingPlan.rvPlanCompanionInvited.visibility =
                            View.VISIBLE
                        else bindingPlan.rvPlanCompanionInvited.visibility = View.GONE
                        bindingPlan.tvInviteValue.text = invitedUserList.size.toString()
                    } else {
                        Toast.makeText(
                            context,
                            "${user.nickname}님 초대를 취소하였습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                        invitedUserList.remove(user) // 리스트에 제거
                        notifyDataSetChanged()
                        bindingPlan.rvPlanCompanionInvited.adapter?.notifyDataSetChanged()
                        if (invitedUserList.size == 0) bindingPlan.rvPlanCompanionInvited.visibility =
                            View.GONE
                        bindingPlan.tvInviteValue.text = invitedUserList.size.toString()

                    }
                }
            }

        }

        override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
            holder.bind(users[position])
        }

        override fun getItemCount() = users.size

        inner class SearchViewHolder(private val binding: ItemAddCompanionBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun bind(user: User) {
                with(binding) {
                    if (invitedUserList.contains(user)) {
                        btnAddCompanion.text = "초대 전송됨"
                    } else {
                        btnAddCompanion.text = "초대하기"
                    }
                    binding.tvAddCompanion.text = user.nickname
                    ImageUtil.displayImageFromUrl(
                        binding.ivAddCompanion,
                        user.thumbnail,
                        1000
                    )
                }
            }
        }
    }


    ///////////////////////////////////////////////////////////////////////////////
    inner class InvitedCompanionAdapter :
        RecyclerView.Adapter<InvitedCompanionAdapter.InvitedCompanionHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvitedCompanionHolder {
            val binding = ItemCompanionInvitedBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return InvitedCompanionHolder(binding).also { holder ->
                binding.ivCompanionInvited.setOnLongClickListener {
                    invitedUserList.removeAt(holder.adapterPosition)
                    notifyDataSetChanged()
                    bindingPlan.rvPlanCompanion.adapter?.notifyDataSetChanged()
                    if (invitedUserList.size == 0) bindingPlan.rvPlanCompanionInvited.visibility =
                        View.GONE
                    bindingPlan.tvInviteValue.text = invitedUserList.size.toString()
                    return@setOnLongClickListener true


                }
            }
        }


        override fun onBindViewHolder(holder: InvitedCompanionHolder, position: Int) {
            holder.bind(invitedUserList[position])
        }

        override fun getItemCount() = invitedUserList.size

        inner class InvitedCompanionHolder(private val binding: ItemCompanionInvitedBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun bind(invitedUser: User) {
                ImageUtil.displayImageFromUrl(
                    binding.ivCompanionInvited,
                    invitedUser.thumbnail,
                    1000
                )
            }
        }
    }

}


