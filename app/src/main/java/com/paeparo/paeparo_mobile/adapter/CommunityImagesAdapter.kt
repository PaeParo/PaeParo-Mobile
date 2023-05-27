package com.paeparo.paeparo_mobile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paeparo.paeparo_mobile.databinding.ItemCommunityImagesBinding
import com.paeparo.paeparo_mobile.util.ImageUtil


class CommunityImagesAdapter(private var imageList: MutableList<String> = mutableListOf()) :
    RecyclerView.Adapter<CommunityImagesAdapter.CommunityViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityViewHolder {
        val binding =
            ItemCommunityImagesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommunityViewHolder(binding)
    }

    fun getTagList(): List<String> {
        return imageList
    }

    fun addTag(tag: String) {
        imageList.add(tag)
        notifyItemInserted(imageList.size - 1)
    }

    override fun onBindViewHolder(holder: CommunityViewHolder, position: Int) {
        holder.bind(imageList[position])
    }

    override fun getItemCount(): Int = imageList.size

    class CommunityViewHolder(private val binding: ItemCommunityImagesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(path: String) {
            ImageUtil.displayImageFromUrl(binding.imageView, path)
        }
    }
}