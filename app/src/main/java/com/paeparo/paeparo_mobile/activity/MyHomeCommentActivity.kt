package com.paeparo.paeparo_mobile.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.paeparo.paeparo_mobile.adapter.MyHomeCommentAdapter
import com.paeparo.paeparo_mobile.application.getPaeParo
import com.paeparo.paeparo_mobile.databinding.ActivityMyHomeCommentBinding
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import kotlinx.coroutines.launch
import timber.log.Timber

class MyHomeCommentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyHomeCommentBinding
    private lateinit var commentAdapter:MyHomeCommentAdapter
    private  lateinit var verticalManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyHomeCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        verticalManager = LinearLayoutManager(this@MyHomeCommentActivity)
        verticalManager.orientation = LinearLayoutManager.VERTICAL

        binding.myhomeCommentRecyclerview.apply {
            layoutManager = verticalManager
        }

        //작성한 댓글 가져오기
        lifecycleScope.launch {
            val writeComments = FirebaseManager.getUserComments(getPaeParo().userId)
            if (writeComments.isSuccess) {
                commentAdapter = MyHomeCommentAdapter(writeComments.data!!)
                binding.myhomeCommentRecyclerview.adapter = commentAdapter
            } else {
                Timber.e("null comment")
            }
        }

        with(binding){
            back.setOnClickListener {
                finish()
            }
            comment.setText("작성한 댓글")
        }
    }
}