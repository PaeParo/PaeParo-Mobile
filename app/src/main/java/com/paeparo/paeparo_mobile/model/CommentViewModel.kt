package com.paeparo.paeparo_mobile.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * CommentViewModel class
 *
 * 불러온 댓글 정보를 저장 및 관리하는 ViewModel 클래스
 */
class CommentViewModel(private val postId: String) : ViewModel() {
    /**
     * 불러온 댓글 중 가장 마지막 댓글의 DocumentSnapshot
     */
    private var lastVisibleComment: DocumentSnapshot? = null

    /**
     * 새로 불러온 댓글 목록
     */
    private val _newCommentList = MutableLiveData<List<Comment>>()
    val newCommentList: LiveData<List<Comment>> = _newCommentList

    private var loadCommentsJob: Job? = null

    /**
     * 새로운 댓글 목록을 가져오는 함수
     */
    fun loadComments() {
        // 이전에 실행 중이던 작업이 있다면 취소
        loadCommentsJob?.cancel()
        loadCommentsJob = viewModelScope.launch {
            val commentListResult =
                FirebaseManager.getPostCommentsInRange(
                    postId = postId,
                    startCommentSnapshot = lastVisibleComment,
                    limit = COMMENT_LOAD_SIZE
                )

            if (commentListResult.isSuccess) { // 게시물 목록을 성공적으로 불러왔을 경우
                val commentListData = commentListResult.data
                val loadedCommentList = commentListData!!["comments"] as List<Comment>
                _newCommentList.value = loadedCommentList

                if (loadedCommentList.isNotEmpty()) { // 불러온 게시물이 한 개 이상 있을 경우
                    lastVisibleComment =
                        commentListData["last_comment_snapshot"] as DocumentSnapshot
                }
            }
        }
    }

    companion object {
        const val COMMENT_LOAD_SIZE: Long = 10
    }
}