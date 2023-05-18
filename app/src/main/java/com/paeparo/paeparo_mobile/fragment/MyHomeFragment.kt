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
import com.paeparo.paeparo_mobile.activity.MyHomeSettingsActivity
import com.paeparo.paeparo_mobile.application.getPaeParo
import com.paeparo.paeparo_mobile.databinding.FragmentMyHomeBinding
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import com.paeparo.paeparo_mobile.util.ImageUtil


class MyHomeFragment : Fragment() {
    private var _binding: FragmentMyHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyHomeBinding.inflate(inflater)

        with(binding) {

            name.text = requireContext().getPaeParo().nickname

            //설정
            settings.setOnClickListener {
                val intent = Intent(activity, MyHomeSettingsActivity::class.java)
                startActivity(intent)
            }

            //자주듣는질문(FAQ)
            faq.setOnClickListener {
                val intent = Intent(activity, MyHomeFaqActivity::class.java)
                startActivity(intent)
            }

            //댓글 단 글
            comment.setOnClickListener {
                val intent = Intent(activity, MyHomeCommentActivity::class.java)
                startActivity(intent)
            }

            //찜한 일정
            like.setOnClickListener {
                val intent = Intent(activity, MyHomeLikeActivity::class.java)
                startActivity(intent)
            }
        }
        ImageUtil.displayImageFromUrl(
            binding.profileImageView,
            FirebaseManager.getCurrentUserProfileUrl()
        )

        return binding.root
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}