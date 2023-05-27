package com.paeparo.paeparo_mobile.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paeparo.paeparo_mobile.databinding.ItemMyhomeLikeBinding
import com.paeparo.paeparo_mobile.model.Post
import com.paeparo.paeparo_mobile.util.ImageUtil
import java.text.SimpleDateFormat

class MyHomeLikeAdapter(private var likedPostList: MutableList<Post>) :
    RecyclerView.Adapter<MyHomeLikeAdapter.MyHomeLikeViewHoloder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHomeLikeViewHoloder {
        val binding =
            ItemMyhomeLikeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyHomeLikeViewHoloder(binding)
    }

    override fun getItemCount(): Int = likedPostList.size

    override fun onBindViewHolder(holder: MyHomeLikeViewHoloder, position: Int) {
        holder.bind(likedPostList[position])
    }

    class MyHomeLikeViewHoloder(private val binding: ItemMyhomeLikeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            val time = SimpleDateFormat("yyyy-MM-dd")
            val toDate = time.format(post.createdAt.toDate())
            Log.d("data",toDate)
            ImageUtil.displayImageFromUrl(binding.bgLikeImage, post.images.toString())
            binding.likeTitle.text = post.title
            binding.time.text = toDate.toString()
            binding.likeNum.text = post.likes.toString()
        }
    }
}