package com.paeparo.paeparo_mobile.adapter

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paeparo.paeparo_mobile.databinding.ItemPostBinding
import com.paeparo.paeparo_mobile.fragment.OnPostClickListener
import com.paeparo.paeparo_mobile.model.Post
import com.paeparo.paeparo_mobile.util.ImageUtil
import kotlin.math.abs

/**
 * PostAdapter class
 *
 * CommunityFragment의 게시물 목록을 관리하는 Adapter 클래스
 */
class PostAdapter(private val onPostClickListener: OnPostClickListener) :
    RecyclerView.Adapter<PostAdapter.ViewHolder>() {
    /**
     * Post 목록
     */
    private var postList = mutableListOf<Post>()

    /**
     * Post 위치 및 대표 이미지를 담고 있는 ViewHolder
     *
     * @property binding ItemPostBinding
     */
    inner class ViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.ivItemPostMainImage.transitionName = "transition_${post.postId}"

            binding.root.setOnClickListener {
                onPostClickListener.onPostClicked(post, binding.ivItemPostMainImage)
            }
            binding.tvItemPostRegion.text = post.region
            if (post.images.isNotEmpty()) { // 게시물에 연결된 이미지가 있을 경우
                // 게시물의 대표 이미지(첫 번째 이미지)를 표시
                ImageUtil.displayImageFromUrl(binding.ivItemPostMainImage, post.images.first())
            }
            binding.root.layoutParams.height = getRandomHeight(post.postId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPostBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(postList[position])
    }

    /**
     * 240 ~ 360 사이의 랜덤한 높이 값을 반환해주는 함수
     *
     * @param postId 게시물 ID
     * @return 랜덤한 높이 값
     */
    private fun getRandomHeight(postId: String): Int {
        return ((240 + 40 * (abs(postId.hashCode()) % 4)) * Resources.getSystem().displayMetrics.density).toInt()
    }

    /**
     * 새로운 Post 목록을 기존의 목록에 추가하는 함수
     *
     * @param newPostList 새로운 Post 목록
     */
    fun addPostList(newPostList: List<Post>) {
        postList.addAll(newPostList)
        notifyItemRangeInserted(postList.size, newPostList.size)
    }

    /**
     * Post 목록을 초기화하는 함수
     */
    fun clearPostList() {
        postList.clear()
        notifyDataSetChanged()
    }
}