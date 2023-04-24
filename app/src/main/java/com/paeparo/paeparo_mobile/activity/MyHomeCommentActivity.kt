package com.paeparo.paeparo_mobile.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.paeparo.paeparo_mobile.adapter.MyHomeCommentAdapter
import com.paeparo.paeparo_mobile.databinding.ActivityMyHomeCommentBinding

class MyHomeCommentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyHomeCommentBinding
    private lateinit var commentAdapter:MyHomeCommentAdapter
    private  lateinit var verticalManager: LinearLayoutManager
    var datas = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        datas = mutableListOf("test1","test2")
        binding = ActivityMyHomeCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        commentAdapter = MyHomeCommentAdapter(datas)

        verticalManager = LinearLayoutManager(this@MyHomeCommentActivity)
        verticalManager.orientation = LinearLayoutManager.VERTICAL

        binding.myhomeCommentRecyclerview.apply{
            layoutManager = verticalManager
            adapter = commentAdapter
        }


    }
}