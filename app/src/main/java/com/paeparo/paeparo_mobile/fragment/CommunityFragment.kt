package com.paeparo.paeparo_mobile.fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.paeparo.paeparo_mobile.adapter.PostAdapter
import com.paeparo.paeparo_mobile.databinding.FragmentCommunityBinding
import com.paeparo.paeparo_mobile.model.PostViewModel

class CommunityFragment : Fragment() {
    private var _binding: FragmentCommunityBinding? = null
    private val binding get() = _binding!!

    /**
     * PostViewModel
     */
    private lateinit var postViewModel: PostViewModel

    /**
     * PostAdapter
     */
    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommunityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPostViewModel()
        setupPostAdapter()
        setupUIListener()

        postViewModel.loadPosts()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * PostViewModel 초기화 함수
     */
    private fun setupPostViewModel() {
        postViewModel = ViewModelProvider(this)[PostViewModel::class.java]
        postViewModel.newPostList.observe(viewLifecycleOwner) { postList ->
            postAdapter.addPostList(postList)
        }

        postViewModel.region.observe(viewLifecycleOwner) {
            postAdapter.clearPostList()
            postViewModel.clearLastVisiblePost()
            postViewModel.loadPosts()
        }
    }

    /**
     * PostAdapter 초기화 함수
     */
    private fun setupPostAdapter() {
        postAdapter = PostAdapter()
        binding.rvCommunityPostList.addItemDecoration(CustomItemDecoration(16))
        binding.rvCommunityPostList.layoutManager =
            StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        binding.rvCommunityPostList.adapter = postAdapter
    }

    /**
     * UI Listener 초기화 함수
     */
    private fun setupUIListener() {
        binding.rvCommunityPostList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as StaggeredGridLayoutManager
                val lastVisibleItem =
                    layoutManager.findLastVisibleItemPositions(null).maxOrNull() ?: 0

                if (layoutManager.itemCount <= (lastVisibleItem + 4)) {
                    // 현재 새로운 Post 목록을 로딩 중이 아니고 스크롤이 끝에 도달했을 경우, 새로운 Post 목록 로딩
                    postViewModel.loadPosts()
                }
            }
        })

        binding.svCommunityRegion.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                postViewModel.setRegion(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    postViewModel.setRegion(null)
                }
                return false
            }
        })
    }

    class CustomItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.left = space
            outRect.right = space
            outRect.bottom = space

            // 첫 번째 아이템이라면 위쪽 간격도 추가한다.
            if (parent.getChildAdapterPosition(view) > 1) {
                outRect.top = space
            }
        }
    }
}