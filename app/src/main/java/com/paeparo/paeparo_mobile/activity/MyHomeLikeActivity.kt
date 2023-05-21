package com.paeparo.paeparo_mobile.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.paeparo.paeparo_mobile.adapter.MyHomeLikeAdapter
import com.paeparo.paeparo_mobile.application.getPaeParo
import com.paeparo.paeparo_mobile.databinding.ActivityMyHomeLikeBinding
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import kotlinx.coroutines.launch
import timber.log.Timber

class MyHomeLikeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyHomeLikeBinding
    private lateinit var likeAdapter:MyHomeLikeAdapter
    private lateinit var verticalManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyHomeLikeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 상단 액션바
        supportActionBar?.setTitle("찜한 일정")

        verticalManager = LinearLayoutManager(this@MyHomeLikeActivity)
        verticalManager.orientation =LinearLayoutManager.VERTICAL

        binding.myhomeLikeRecyclerview.apply {
            layoutManager=verticalManager
        }

        //좋아요한 일정 가져오기
        lifecycleScope.launch {
            val likePosts = FirebaseManager.getUserLikedPosts(getPaeParo().userId)
            if(likePosts.isSuccess){
                likeAdapter = MyHomeLikeAdapter(likePosts.data!!)
                Timber.d("test"+likePosts.data)
                binding.myhomeLikeRecyclerview.adapter = likeAdapter
            }else{
                Timber.e("null like")
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