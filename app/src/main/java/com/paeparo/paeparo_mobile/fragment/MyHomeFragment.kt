package com.paeparo.paeparo_mobile.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.paeparo.paeparo_mobile.databinding.FragmentMyHomeBinding
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import com.paeparo.paeparo_mobile.model.User
import kotlinx.coroutines.*


class MyHomeFragment : Fragment() {
    private var _binding:FragmentMyHomeBinding? = null
    private val binding get()=_binding!!
    private val networkScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        networkScope.launch {
            val result: Result<User> = FirebaseManager.getCurrentUserData(this@MyHomeFragment.requireContext())
            println(result)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding=FragmentMyHomeBinding.inflate(inflater)

        binding!!.fixhome.setOnClickListener{
            binding!!.name.text="fixhome"
        }

        binding!!.settings.setOnClickListener{
            binding!!.name.text="settings"
        }

        binding!!.plan.setOnClickListener{
            binding!!.name.text="plan"
        }

        binding!!.profile.setOnClickListener{
            //val intent = Intent(activity,MyHome::class.java)
            //startActivity(intent)
        }

        binding!!.faq.setOnClickListener{
            binding!!.name.text="faq"
        }
        binding!!.comment.setOnClickListener {
            binding!!.name.text="comment"
        }

        binding!!.like.setOnClickListener{
            binding!!.name.text="like"
        }

        return binding!!.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}