package com.paeparo.paeparo_mobile.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.paeparo.paeparo_mobile.activity.*
import com.paeparo.paeparo_mobile.databinding.FragmentMyHomeBinding
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import com.paeparo.paeparo_mobile.model.User
import kotlinx.coroutines.*


class MyHomeFragment : Fragment() {
    private var _binding:FragmentMyHomeBinding? = null
    private val binding get()=_binding!!
    private val networkScope = CoroutineScope(Dispatchers.IO)
    private lateinit var username : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var user : User
        networkScope.launch {
            val result: Result<User> = FirebaseManager.getCurrentUserData(this@MyHomeFragment.requireContext())
            result.onSuccess {
                user = it as User
                username = user.nickname
                Log.d("USERNAME:",username)
           }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding=FragmentMyHomeBinding.inflate(inflater)

        binding!!.fixhome.setOnClickListener{
            val intent = Intent(activity, MyHomeProfileActivity::class.java)
            startActivity(intent)
            Log.d("USERNAME:",username)
        }

        binding!!.settings.setOnClickListener{
            val intent = Intent(activity, MyHomeSettingsActivity::class.java)
            startActivity(intent)
        }

        binding!!.plan.setOnClickListener{
            binding!!.name.text="plan"
        }

        binding!!.profile.setOnClickListener{
            val intent = Intent(activity, MyHomeProfileActivity::class.java)
            startActivity(intent)
        }

        binding!!.faq.setOnClickListener{
            val intent = Intent(activity, MyHomeFaqActivity::class.java)
            startActivity(intent)
        }
        binding!!.comment.setOnClickListener {
            val intent = Intent(activity, MyHomeCommentActivity::class.java)
            startActivity(intent)
        }

        binding!!.like.setOnClickListener{
            val intent = Intent(activity, MyHomeLikeActivity::class.java)
            startActivity(intent)
        }

        return binding!!.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}