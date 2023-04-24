package com.paeparo.paeparo_mobile.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.paeparo.paeparo_mobile.activity.MyHomeCommentActivity
import com.paeparo.paeparo_mobile.activity.MyHomeFaqActivity
import com.paeparo.paeparo_mobile.activity.MyHomeLikeActivity
import com.paeparo.paeparo_mobile.activity.MyHomeProfileActivity
import com.paeparo.paeparo_mobile.activity.MyHomeSettingsActivity
import com.paeparo.paeparo_mobile.application.getPaeParo
import com.paeparo.paeparo_mobile.databinding.FragmentMyHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel


class MyHomeFragment : Fragment() {
    private var _binding: FragmentMyHomeBinding? = null
    private val binding get() = _binding!!
    private val networkScope = CoroutineScope(Dispatchers.IO)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyHomeBinding.inflate(inflater)

        binding.name.text = requireContext().getPaeParo().nickname

        //프로필 수정
        binding.fixhome.setOnClickListener {
            val intent = Intent(activity, MyHomeProfileActivity::class.java)
            startActivity(intent)
        }

        //설정
        binding.settings.setOnClickListener {
            val intent = Intent(activity, MyHomeSettingsActivity::class.java)
            startActivity(intent)
        }

        //내가 만든 일정
        binding.plan.setOnClickListener {
            binding.name.text = "plan"
        }

        //프로필
        binding.profile.setOnClickListener {
            val intent = Intent(activity, MyHomeProfileActivity::class.java)
            startActivity(intent)
        }

        //자주듣는질문(FAQ)
        binding.faq.setOnClickListener {
            val intent = Intent(activity, MyHomeFaqActivity::class.java)
            startActivity(intent)
        }

        //댓글 단 글
        binding.comment.setOnClickListener {
            val intent = Intent(activity, MyHomeCommentActivity::class.java)
            startActivity(intent)
        }

        //찜한 일정
        binding.like.setOnClickListener {
            val intent = Intent(activity, MyHomeLikeActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onDestroy() {
        networkScope.cancel()
        _binding = null
        super.onDestroy()
    }
}