package com.paeparo.paeparo_mobile.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import com.paeparo.paeparo_mobile.databinding.FragmentPlanCompanionBinding

class PlanCompanionFragment : Fragment() {
    private var _binding : FragmentPlanCompanionBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanCompanionBinding.inflate(inflater,container,false)

        val searchView = binding.svPlanCompanion

        searchView.apply{
            setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    binding.tvPlanCompanion.text =  query.toString()
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    Log.d("PlanCompanionFragment",newText.toString())
                    return true
                }

            })
        }


        return binding.root
    }
}
