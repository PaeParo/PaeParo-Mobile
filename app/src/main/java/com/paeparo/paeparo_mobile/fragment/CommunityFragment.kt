package com.paeparo.paeparo_mobile.fragment

import android.graphics.Rect
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.adapter.OnPostClickListener
import com.paeparo.paeparo_mobile.adapter.PostAdapter
import com.paeparo.paeparo_mobile.databinding.FragmentCommunityBinding
import com.paeparo.paeparo_mobile.model.Post
import com.paeparo.paeparo_mobile.model.PostViewModel

class CommunityFragment : Fragment(), OnPostClickListener {
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
        setupUI()

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
        postAdapter = PostAdapter(this)
        binding.rvCommunityPostList.adapter = postAdapter
    }

    /**
     * UI 초기화 함수
     */
    private fun setupUI() {
        binding.rvCommunityPostList.layoutManager =
            StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)

        binding.rvCommunityPostList.addItemDecoration(object : RecyclerView.ItemDecoration() {
            private val space: Int = 16

            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                outRect.left = space
                outRect.right = space
                outRect.bottom = space

                if (parent.getChildAdapterPosition(view) > 1) { // 첫 번째 아이템일 경우 위쪽 간격도 추가
                    outRect.top = space
                }
            }
        })

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
                binding.svCommunityRegion.clearFocus()
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

    /**
     * Post 아이템 선택 시 해당 아이템의 세부내용 Fragment를 호출하는 함수
     *
     * @param post 선택된 Post 아이템
     */
    override fun onPostClicked(
        post: Post,
        postImageView: ImageView,
        regionIconImageView: ImageView,
        postRegionTextView: TextView
    ) {
        val existingPostFragment = parentFragmentManager.findFragmentByTag("PostFragment")
        val transaction = parentFragmentManager.beginTransaction()
        if (existingPostFragment != null) { // 이미 PostFragment가 존재할 경우 제거
            transaction.remove(existingPostFragment)
        }

        // PostFragment 객체 생성
        val postFragment = PostFragment.newInstance(post)

        // Fragment 전환 시 사용될 Transition 구성
        val enterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        enterTransition.duration = 300
        postFragment.sharedElementEnterTransition = enterTransition
        val returnTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        returnTransition.duration = 300
        postFragment.sharedElementReturnTransition = returnTransition
        transaction.add(R.id.fl_main_view, postFragment)
            .hide(this)
            .addToBackStack(null)
            .setReorderingAllowed(true)
            .addSharedElement(postImageView, "transition_image_${post.postId}")
            .addSharedElement(regionIconImageView, "transition_icon_${post.postId}")
            .addSharedElement(postRegionTextView, "transition_text_${post.postId}")
            .commit()
    }
}