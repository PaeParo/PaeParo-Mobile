package com.paeparo.paeparo_mobile.fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.paeparo.paeparo_mobile.databinding.FragmentPlanInfoBinding

/*
일자별 Event를 표시해주는 RecyclerView
 */
class PlanInfoFragment : Fragment() {
    private var _binding : FragmentPlanInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanInfoBinding.inflate(inflater,container,false)
        return binding.root


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}