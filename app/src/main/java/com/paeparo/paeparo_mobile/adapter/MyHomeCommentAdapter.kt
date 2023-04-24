package com.paeparo.paeparo_mobile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.paeparo.paeparo_mobile.R

class MyHomeCommentAdapter(var datas:MutableList<String>):RecyclerView.Adapter<MyHomeCommentAdapter.MyHomeCommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHomeCommentViewHolder {
        return MyHomeCommentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_myhome_comment,parent,false))
    }

    override fun onBindViewHolder(holder: MyHomeCommentViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    class MyHomeCommentViewHolder(view:View):RecyclerView.ViewHolder(view) {
        val title:TextView = view.findViewById(R.id.title_name)
        fun bind(position: String){
            title.text=position
        }
    }
}