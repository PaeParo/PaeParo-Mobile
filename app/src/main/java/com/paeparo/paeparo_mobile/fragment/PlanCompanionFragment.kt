package com.paeparo.paeparo_mobile.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.activity.PlanGenerateActivity
import com.paeparo.paeparo_mobile.databinding.FragmentPlanCompanionBinding
import com.paeparo.paeparo_mobile.databinding.ItemAddCompanionBinding
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import com.paeparo.paeparo_mobile.model.User
import kotlinx.coroutines.*

class PlanCompanionFragment : Fragment() {
    private var _binding: FragmentPlanCompanionBinding? = null
    private val binding get() = _binding!!
    private var users = listOf<User>()
    private var job: Job? = null
    private val invited_users = emptyList<User>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanCompanionBinding.inflate(inflater, container, false)
        val searchView = binding.svPlanCompanion

        val t = activity as PlanGenerateActivity

        binding.btnCompanionNext.setOnClickListener{
            t.binding.vpPlanGenerate.currentItem++
        }

        searchView.apply {
            // 검색후 엔터 누를시,
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchUser(query)
                    return false
                }

                //
                override fun onQueryTextChange(newText: String?): Boolean {
                    //딜레이후 검색
                    if(job!=null && job!!.isActive) job!!.cancel()
                    if(newText==" ") return true
                    job = lifecycleScope.launch(Dispatchers.IO) {
                        delay(2000)
                        searchUser(newText)
                    }
                    return true
                }

            })

        }

        binding.rvPlanCompanion.apply {
            binding.rvPlanCompanion.layoutManager = LinearLayoutManager(context)
            binding.rvPlanCompanion.adapter = SearchViewAdapter(users)
        }

        return binding.root
    }

    fun searchUser(nickname: String?) {

        CoroutineScope(Dispatchers.IO).launch {
            val result = FirebaseManager.getUsersStartWith(nickname!!)
           if(result.isSuccess){
               withContext(Dispatchers.Main) {
                   binding.rvPlanCompanion.adapter = SearchViewAdapter(result.data as List<User>)
               }
               result.data
           } else {
           }
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
            binding.tvAddCompanion.text = user.nickname.toString() + "\nthumbnail :"+ user.thumbnail.toString()
        }
    }
}


