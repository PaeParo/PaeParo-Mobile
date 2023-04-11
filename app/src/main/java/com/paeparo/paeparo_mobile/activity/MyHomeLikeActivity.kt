package com.paeparo.paeparo_mobile.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.paeparo.paeparo_mobile.adapter.MyHomeLikeAdapter
import com.paeparo.paeparo_mobile.databinding.ActivityMyHomeLikeBinding

class MyHomeLikeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyHomeLikeBinding
    private lateinit var likeAdapter:MyHomeLikeAdapter
    private lateinit var verticalManager: LinearLayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyHomeLikeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        likeAdapter = MyHomeLikeAdapter()

        verticalManager = LinearLayoutManager(this@MyHomeLikeActivity)
        verticalManager.orientation =LinearLayoutManager.VERTICAL

        binding.myhomeLikeRecyclerview.apply {
            layoutManager=verticalManager
            adapter = likeAdapter
        }
    }
}