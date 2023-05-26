package com.paeparo.paeparo_mobile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.paeparo.paeparo_mobile.databinding.ItemPostImageBinding
import com.paeparo.paeparo_mobile.util.ImageUtil
import com.smarteist.autoimageslider.SliderViewAdapter


class PostImageAdapter(private val imageUrlList: MutableList<String>) :
    SliderViewAdapter<PostImageAdapter.PostImageViewHolder>() {

    inner class PostImageViewHolder(private val binding: ItemPostImageBinding) :
        ViewHolder(binding.root) {
        fun bind(imageUrl: String) {
            ImageUtil.displayImageFromUrl(binding.ivItemPostImage, imageUrl)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup): PostImageViewHolder {
        return PostImageViewHolder(
            ItemPostImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: PostImageViewHolder, position: Int) {
        holder.bind(imageUrlList[position])
    }

    override fun getCount(): Int {
        return imageUrlList.size
    }
}