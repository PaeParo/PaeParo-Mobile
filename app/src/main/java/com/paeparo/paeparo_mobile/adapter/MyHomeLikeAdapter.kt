package com.paeparo.paeparo_mobile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.paeparo.paeparo_mobile.R

class MyHomeLikeAdapter:RecyclerView.Adapter<MyHomeLikeAdapter.MyHomeLikeViewHoloder>() {
    //테스트 list
    var list = listOf(1,2,3,4,5,6,7,8,9,10)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHomeLikeViewHoloder {
        return MyHomeLikeViewHoloder(LayoutInflater.from(parent.context).inflate(R.layout.item_myhome_like,parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyHomeLikeViewHoloder, position: Int) {
        holder.bind(list[position])
    }

    class MyHomeLikeViewHoloder(view: View):RecyclerView.ViewHolder(view) {
        val button:TextView =view.findViewById(R.id.button)
        fun bind(position: Int){
            button.text="$position"
            button.setOnClickListener(){
                button.text="클릭했다!"
            }
        }
    }
}