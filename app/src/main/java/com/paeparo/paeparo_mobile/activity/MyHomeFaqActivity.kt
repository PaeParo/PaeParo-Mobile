package com.paeparo.paeparo_mobile.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.paeparo.paeparo_mobile.adapter.MyHomeFaqAdapter
import com.paeparo.paeparo_mobile.databinding.ActivityMyHomeFaqBinding

class MyHomeFaqActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyHomeFaqBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyHomeFaqBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.faqRecyclerView.apply{
            adapter=MyHomeFaqAdapter()
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }

        with(binding){
            back.setOnClickListener {
                finish()
            }
            faq.setText("FAQ")
        }
    }
}