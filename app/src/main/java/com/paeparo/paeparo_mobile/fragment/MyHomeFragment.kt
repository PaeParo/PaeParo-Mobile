package com.paeparo.paeparo_mobile.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.gms.dynamic.SupportFragmentWrapper
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.databinding.FragmentMyHomeBinding

class MyHomeFragment : Fragment() {
    private var binding:FragmentMyHomeBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentMyHomeBinding.inflate(inflater)

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
            binding!!.name.text="profile"
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
}