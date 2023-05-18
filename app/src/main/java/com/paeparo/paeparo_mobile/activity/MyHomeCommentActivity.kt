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

        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 상단 액션바

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
                Timber.d("test"+writeComments.data)
                binding.myhomeCommentRecyclerview.adapter = commentAdapter
            } else {
                Timber.e("null comment")
            }
        }


    }

    //상단 바 제어
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed() // 뒤로가기 동작 수행
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}