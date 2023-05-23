package com.paeparo.paeparo_mobile.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import kotlinx.coroutines.launch

/**
 * PostViewModel class
 *
 * 불러온 여행 정보를 저장 및 관리하는 ViewModel 클래스
 */
class PostViewModel : ViewModel() {
    /**
     * 불러온 게시물 중 가장 마지막 게시물의 DocumentSnapshot
     */
    private var lastVisiblePost: DocumentSnapshot? = null

    /**
     * 새로 불러온 게시물 목록
     */
    private val _newPostList = MutableLiveData<List<Post>>()
    val newPostList: LiveData<List<Post>> = _newPostList

    /**
     * Firebase 관련 처리 상태
     */
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    /**
     * 새로운 게시물 목록을 가져오는 함수
     */
    fun loadPosts() {
        // 이미 데이터가 로딩 중일 경우 새로 로딩하지 않음
        if (_loading.value == true) return

        _loading.value = true
        viewModelScope.launch {
            val postListResult = FirebaseManager.getPostsInRange(lastVisiblePost, POST_LOAD_SIZE)
            val postListData = postListResult.data

            if (postListResult.isSuccess) { // 게시물 목록을 성공적으로 불러왔을 경우
                val loadedPostList = postListData!!["posts"] as List<Post>
                _newPostList.value = loadedPostList

                if (loadedPostList.isNotEmpty()) { // 불러온 게시물이 한 개 이상 있을 경우
                    lastVisiblePost = postListData["last_post_snapshot"] as DocumentSnapshot
                }
            }

            _loading.value = false
        }
    }

    companion object {
        const val POST_LOAD_SIZE: Long = 8
    }
}