package com.paeparo.paeparo_mobile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.paeparo.paeparo_mobile.databinding.ItemPostImageBinding
import com.paeparo.paeparo_mobile.util.ImageUtil
import com.smarteist.autoimageslider.SliderViewAdapter

/**
 * PostImageAdapter class
 *
 * 게시물 세부정보 화면에서 게시물 이미지 목록을 관리하는 Adapter 클래스
 *
 * @property imageUrlList 표시할 이미지 URL 목록
 */
class PostImageAdapter(private val imageUrlList: MutableList<String>) :
    SliderViewAdapter<PostImageAdapter.PostImageViewHolder>() {

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
    
    /**
     * 게시물 이미지를 담고 있는 ViewHolder
     *
     * @property binding ItemPostImageBinding
     */
    inner class PostImageViewHolder(private val binding: ItemPostImageBinding) :
        ViewHolder(binding.root) {
        fun bind(imageUrl: String) {
            ImageUtil.displayImageFromUrl(binding.ivItemPostImage, imageUrl)
        }
    }
}