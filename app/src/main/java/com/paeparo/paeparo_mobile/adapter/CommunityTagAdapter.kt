package com.paeparo.paeparo_mobile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paeparo.paeparo_mobile.databinding.ItemCommunityTagBinding

class CommunityTagAdapter(private var tagList: MutableList<String> = mutableListOf()) :
    RecyclerView.Adapter<CommunityTagAdapter.CommunityViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityViewHolder {
        val binding = ItemCommunityTagBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CommunityViewHolder(binding)
    }

    fun getTagList(): List<String> {
        return tagList
    }

    fun addTag(tag: String) {
        tagList.add(tag)
        notifyItemInserted(tagList.size - 1)
    }

    override fun onBindViewHolder(holder: CommunityViewHolder, position: Int) {
        holder.bind(tagList[position])
    }

    override fun getItemCount(): Int = tagList.size

    class CommunityViewHolder(private val binding: ItemCommunityTagBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tag: String) {
            binding.tag.text = tag
        }
    }
}