package com.paeparo.paeparo_mobile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paeparo.paeparo_mobile.databinding.ItemCommentBinding
import com.paeparo.paeparo_mobile.model.Comment
import com.paeparo.paeparo_mobile.util.ImageUtil

/**
 * CommentAdapter class
 *
 * PostFragment의 댓글 목록을 관리하는 Adapter 클래스
 */
class CommentAdapter :
    RecyclerView.Adapter<CommentAdapter.ViewHolder>() {
    /**
     * Comment 목록
     */
    private var commentList = mutableListOf<Comment>()

    /**
     * Comment 정보를 담고 있는 ViewHolder
     *
     * @property binding ItemCommentBinding
     */
    inner class ViewHolder(private val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment) {
            ImageUtil.displayImageFromUrl(
                binding.ivCommentUserThumbnail,
                comment.userThumbnail,
                1000
            )
            binding.tvCommentNickname.text = comment.nickname
            binding.tvCommentContent.text = comment.content
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCommentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(commentList[position])
    }

    /**
     * 새로운 Comment 목록을 기존의 목록에 추가하는 함수
     *
     * @param newCommentList 새로운 Comment 목록
     */
    fun addCommentList(newCommentList: List<Comment>) {
        commentList.addAll(newCommentList)
        notifyItemRangeInserted(commentList.size, newCommentList.size)
    }

    /**
     * 기존의 Comment 목록을 새로운 Comment 목록으로 교체하는 함수
     *
     * @param commentList 새로운 Comment 목록
     */
    fun replaceCommentList(commentList: List<Comment>) {
        this.commentList = commentList.toMutableList()
        notifyDataSetChanged()
    }
}