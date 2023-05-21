package com.paeparo.paeparo_mobile.fragment

import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.activity.PlanGenerateActivity
import com.paeparo.paeparo_mobile.application.PaeParo
import com.paeparo.paeparo_mobile.databinding.FragmentPlanCompanionBinding
import com.paeparo.paeparo_mobile.databinding.ItemAddCompanionBinding
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import com.paeparo.paeparo_mobile.model.User
import com.paeparo.paeparo_mobile.util.ImageUtil
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.function.Consumer
import java.util.function.Predicate

class PlanCompanionFragment : Fragment() {
    companion object{
        const val DELAY_BEFORE_SEARCH = 5000L
    }

    private var _binding: FragmentPlanCompanionBinding? = null
    private val binding get() = _binding!!
    private var users = listOf<User>()
    private var job: Job? = null // 단어가 수정될 떄, 기다리는 Job
    private var previousQuery : String? = null // 이전 검색문
    private val invited_users = emptyList<User>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanCompanionBinding.inflate(inflater, container, false)
        val activity = activity as PlanGenerateActivity
        bind(activity)
        return binding.root
    }

    fun searchUser(userName: String?) {
        val filteredName = userName?.replace("\\s+".toRegex(), "") // 공백 제거
        if (filteredName == "") return // 공백의 문자만 존재할 경우
        if(filteredName.equals(previousQuery)) return // 이전 검색과 같을 경우,
        previousQuery = filteredName
        CoroutineScope(Dispatchers.IO).launch {
            val result = FirebaseManager.getUsersStartWith(filteredName!!)
            val data = result.data ?: return@launch
            // 결과가 없을 경우,
            withContext(Dispatchers.Main) {
                Timber.d("검색중")
                binding.rvPlanCompanion.adapter = SearchViewAdapter(result.data)
            }
        }
    }

    private fun bind(activity: PlanGenerateActivity) {
        with(binding) {
            btnCompanionNext.setOnClickListener {
                activity.binding.vpPlanGenerate.currentItem++
            }
            svPlanCompanion.setOnQueryTextListener(SeachUserListner())
            // SearchView 글자 색 바꾸기
            val editText = svPlanCompanion.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
            editText.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            editText.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            editText.setTypeface(null, Typeface.NORMAL) // 이텔릭체 제거
            editText.hint = "닉네임을 입력해주세요."



            rvPlanCompanion.layoutManager = LinearLayoutManager(context)
            rvPlanCompanion.adapter = SearchViewAdapter(users)
        }
    }

    inner class SearchViewAdapter(private val users: List<User>) :
        RecyclerView.Adapter<SearchViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
            val binding =
                ItemAddCompanionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return SearchViewHolder(
                binding
            ).also { holder ->
                binding.btnAddCompanion.setOnClickListener {

                }
            }

        }

        override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
            holder.bind(users[position])
        }

        override fun getItemCount() = users.size

    }

    inner class SearchViewHolder(private val binding: ItemAddCompanionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.tvAddCompanion.text = user.nickname
            ImageUtil.displayImageFromUrl(
                binding.ivAddCompanion,
                user.thumbnail
            )
        }
    }

    inner class SeachUserListner : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            searchUser(query)
            return false
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            if(job!=null && job!!.isActive) job!!.cancel()
            job = lifecycleScope.launch(Dispatchers.IO) {
                delay(DELAY_BEFORE_SEARCH)
                searchUser(newText)
            }
            return true
        }
    }
}


