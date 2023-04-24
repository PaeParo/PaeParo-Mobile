package com.paeparo.paeparo_mobile.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.paeparo.paeparo_mobile.TestMyHomeData
import com.paeparo.paeparo_mobile.adapter.MyHomeLikeAdapter
import com.paeparo.paeparo_mobile.adapter.SwipeToDeleteCallback
import com.paeparo.paeparo_mobile.databinding.ActivityMyHomeLikeBinding

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

        //임시 데이터
        datas.apply {
            add(TestMyHomeData(1))
            add(TestMyHomeData(2))
            add(TestMyHomeData(3))
        }

        //아이템 삭제
        val swipeToDeleteCallBack = object : SwipeToDeleteCallback(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                datas.removeAt(position)
                binding.myhomeLikeRecyclerview.adapter?.notifyItemRemoved(position)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallBack)
        itemTouchHelper.attachToRecyclerView(binding.myhomeLikeRecyclerview)
    }
}