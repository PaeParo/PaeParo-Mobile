package com.paeparo.paeparo_mobile.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.paeparo.paeparo_mobile.databinding.ActivityMyHomeFaqBinding

class MyHomeFaqActivity : AppCompatActivity() {
    private val binding: ActivityMyHomeFaqBinding by lazy { ActivityMyHomeFaqBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.layout01.setOnClickListener {
            if(binding.layoutDetail01.visibility == View.VISIBLE) {
                binding.layoutDetail01.visibility = View.GONE
                binding.layoutBtn01.animate().apply {
                    duration = 300
                    rotation(0f)
                }
            } else {
                binding.layoutDetail01.visibility = View.VISIBLE
                binding.layoutBtn01.animate().apply {
                    duration = 300
                    rotation(180f)
                }
            }
        }
    }
}