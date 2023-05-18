package com.paeparo.paeparo_mobile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paeparo.paeparo_mobile.databinding.ItemMyhomeCommentBinding
import com.paeparo.paeparo_mobile.model.Comment
import timber.log.Timber

class MyHomeCommentAdapter(private var writeCommentList:MutableList<Comment>):RecyclerView.Adapter<MyHomeCommentAdapter.MyHomeCommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHomeCommentViewHolder {
        val binding = ItemMyhomeCommentBinding.inflate(LayoutInflater.from(parent.context),parent,false,)
        return MyHomeCommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyHomeCommentViewHolder, position: Int) {
        holder.bind(writeCommentList[position])
    }

    override fun getItemCount(): Int = writeCommentList.size


    class MyHomeCommentViewHolder(private  val binding: ItemMyhomeCommentBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment){
            with(binding){
                commentTitle.text = comment.nickname
                this.comment.text=comment.content
            }
        }
    }
}