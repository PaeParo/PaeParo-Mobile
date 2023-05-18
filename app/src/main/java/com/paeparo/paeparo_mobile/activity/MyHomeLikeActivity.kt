package com.paeparo.paeparo_mobile.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.TestMyHomeData
import com.paeparo.paeparo_mobile.adapter.MyHomeLikeAdapter
import com.paeparo.paeparo_mobile.application.getPaeParo
import com.paeparo.paeparo_mobile.databinding.ActivityMyHomeLikeBinding
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class MyHomeLikeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyHomeLikeBinding
    private lateinit var likeAdapter:MyHomeLikeAdapter
    private lateinit var verticalManager: LinearLayoutManager
    val datas = mutableListOf<TestMyHomeData>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyHomeLikeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        likeAdapter = MyHomeLikeAdapter(datas)

        verticalManager = LinearLayoutManager(this@MyHomeLikeActivity)
        verticalManager.orientation =LinearLayoutManager.VERTICAL

        binding.myhomeLikeRecyclerview.apply {
            layoutManager=verticalManager
            adapter = likeAdapter
        }

        //좋아요한 일정 가져오기
        CoroutineScope(Dispatchers.IO).launch {
            val result = FirebaseManager.getUser(getPaeParo().userId)
            val userId = if(result.isSuccess){
                result.data?.userId
            } else {
                Timber.e("null userId")
            }
            val result_like = FirebaseManager.getUserLikedPosts(userId as String)
            val like = if(result_like.isSuccess){
                Timber.d("like is "+result_like.data)
            }else{
                Timber.e("null like")
            }
        }

        //임시 데이터
        datas.apply {

            add(TestMyHomeData(R.drawable.ic_menu_trip_filled,"친구들이랑 함께 다녀옴","23-05-15"))
            add(TestMyHomeData(R.drawable.ic_menu_trip_filled,"시원한 저녁 떠나는 여행","23-05-16"))
        }

    }
}