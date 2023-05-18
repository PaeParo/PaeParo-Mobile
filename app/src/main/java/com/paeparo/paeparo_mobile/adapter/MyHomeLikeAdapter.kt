package com.paeparo.paeparo_mobile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paeparo.paeparo_mobile.TestMyHomeData
import com.paeparo.paeparo_mobile.databinding.ItemMyhomeLikeBinding
import timber.log.Timber

class MyHomeLikeAdapter(private var planList:MutableList<TestMyHomeData>):RecyclerView.Adapter<MyHomeLikeAdapter.MyHomeLikeViewHoloder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHomeLikeViewHoloder {
        val binding = ItemMyhomeLikeBinding.inflate(LayoutInflater.from(parent.context),parent,false,)
        Timber.d("onCreateViewHolder ")
        return MyHomeLikeViewHoloder(binding).also {holder ->

        }
    }

    override fun getItemCount(): Int = planList.size

    override fun onBindViewHolder(holder: MyHomeLikeViewHoloder, position: Int) {
        Timber.d("test onBindViewHolder")
        holder.bind(planList[position])
    }

    class MyHomeLikeViewHoloder(private  val binding: ItemMyhomeLikeBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(post: TestMyHomeData){
            with(binding){
                likePlanImage.setImageResource(post.image)
                title.text = post.title
                date.text= post.date
            }
        }
    }
}