package com.paeparo.paeparo_mobile.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.paeparo.paeparo_mobile.activity.*
import com.paeparo.paeparo_mobile.application.getPaeParo
import com.paeparo.paeparo_mobile.databinding.FragmentMyHomeBinding
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import com.paeparo.paeparo_mobile.model.User
import kotlinx.coroutines.*


class MyHomeFragment : Fragment() {
    private var _binding: FragmentMyHomeBinding? = null
    private val binding get() = _binding!!
    private val networkScope = CoroutineScope(Dispatchers.IO)
    private lateinit var user: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val job =
            networkScope.launch {
                FirebaseManager.getUser(this@MyHomeFragment.requireContext().getPaeParo().userId)
                    .onSuccess {
                        user = it
                    }
            }

        runBlocking {
            job.join()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyHomeBinding.inflate(inflater)

        binding.name.text = user.nickname

        binding.fixhome.setOnClickListener {
            val intent = Intent(activity, MyHomeProfileActivity::class.java)
            startActivity(intent)
        }

        binding.settings.setOnClickListener {
            val intent = Intent(activity, MyHomeSettingsActivity::class.java)
            startActivity(intent)
        }

        binding.plan.setOnClickListener {
            binding.name.text = "plan"
        }

        binding.profile.setOnClickListener {
            val intent = Intent(activity, MyHomeProfileActivity::class.java)
            startActivity(intent)
        }

        binding.faq.setOnClickListener {
            val intent = Intent(activity, MyHomeFaqActivity::class.java)
            startActivity(intent)
        }
        binding.comment.setOnClickListener {
            val intent = Intent(activity, MyHomeCommentActivity::class.java)
            startActivity(intent)
        }

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