package com.paeparo.paeparo_mobile.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.activity.OnPostFragmentInteractionListener
import com.paeparo.paeparo_mobile.activity.PlanActivity
import com.paeparo.paeparo_mobile.adapter.CommentAdapter
import com.paeparo.paeparo_mobile.adapter.PostImageAdapter
import com.paeparo.paeparo_mobile.application.getPaeParo
import com.paeparo.paeparo_mobile.databinding.FragmentPostBinding
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import com.paeparo.paeparo_mobile.model.Comment
import com.paeparo.paeparo_mobile.model.CommentViewModel
import com.paeparo.paeparo_mobile.model.Post
import com.paeparo.paeparo_mobile.model.Trip
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import kotlinx.coroutines.launch
import kotlin.math.abs


class PostFragment : Fragment() {
    /**
     * PostFragment 표시 시 BottomNavigationView을 숨기기 위한 Listener
     */
    private var listener: OnPostFragmentInteractionListener? = null
    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!

    /**
     * 현재 게시물의 좋아요 여부
     */
    private var liked = false

    /**
     * 표시할 Post 객체
     */
    private lateinit var post: Post

    /**
     * CommentViewModel
     */
    private lateinit var commentViewModel: CommentViewModel

    /**
     * CommentAdapter
     */
    private lateinit var commentAdapter: CommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        post = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("post", Post::class.java)!!
        } else {
            arguments?.getParcelable("post")!!
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    listener?.onPostFragmentDismissed()
                    parentFragmentManager.popBackStack()
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        listener?.onPostFragmentDisplayed()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Transition 구성
        binding.svPostImages.transitionName = "transition_image_${post.postId}"
        binding.tvPostRegion.transitionName = "transition_text_${post.postId}"

        setupCommentViewModel()
        setupCommentAdapter()
        setupListener()
        setupUI()

        lifecycleScope.launch {
            val likedResult =
                FirebaseManager.isPostLikedByUser(requireContext().getPaeParo().userId, post.postId)
            liked = if (likedResult.isSuccess && likedResult.data!!) {
                binding.ivPostLike.setImageResource(R.drawable.ic_like)
                true
            } else {
                binding.ivPostLike.setImageResource(R.drawable.ic_like_empty)
                false
            }
        }

        commentViewModel.loadComments(true)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnPostFragmentInteractionListener) {
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * CommentViewModel 초기화 함수
     */
    private fun setupCommentViewModel() {
        commentViewModel = CommentViewModel(post.postId)
        commentViewModel.newCommentList.observe(viewLifecycleOwner) { commentList ->
            if (commentViewModel.resetList) {
                commentAdapter.replaceCommentList(commentList)
            } else {
                commentAdapter.addCommentList(commentList)
            }
        }
    }

    /**
     * CommentAdapter 초기화 함수
     */
    private fun setupCommentAdapter() {
        commentAdapter = CommentAdapter()
        binding.rvPostCommentList.adapter = commentAdapter
    }

    /**
     * Listener 및 Gesture Detector 설정
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setupListener() {
        // 뒤로가기 버튼 Listener 추가
        binding.ivPostBack.setOnClickListener {
            listener?.onPostFragmentDismissed()
            parentFragmentManager.popBackStack()
        }

        // 게시물과 연결된 여행 일정 버튼 Listener 추가
        binding.ivPostTripSchedule.setOnClickListener {
            lifecycleScope.launch {
                val tripResult = FirebaseManager.getTrip(post.tripId)

                if (tripResult.isSuccess) {
                    val context = it.context
                    val intent = Intent(context, PlanActivity::class.java)
                    with(Bundle()) {
                        this.putParcelable("trip", tripResult.data as Trip)
                        intent.putExtra("tripBundle", this)
                    }
                    context.startActivity(intent)
                } else {
                    Snackbar.make(it, "여행 일정을 불러오지 못했습니다", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        // 좋아요 버튼 Listener 추가 및 중복 처리 방지
        binding.llPostLike.setOnClickListener(object : View.OnClickListener {
            private var lastClickTime = 0L
            private val intervalMillis = 1000L

            override fun onClick(v: View) {
                val currentTimeMillis = System.currentTimeMillis()
                if (currentTimeMillis - lastClickTime >= intervalMillis) {
                    lastClickTime = currentTimeMillis

                    lifecycleScope.launch {
                        if (!liked) { // 현재 게시물을 좋아요 하지 않은 상태일 경우
                            if (FirebaseManager.likePost(
                                    post.postId,
                                    requireContext().getPaeParo().userId
                                ).isSuccess
                            ) {
                                binding.ivPostLike.setImageResource(R.drawable.ic_like)
                                post.likes += 1
                                binding.tvPostLikeCount.text = post.likes.toString()
                                liked = true
                            }
                        } else { // 현재 게시물을 좋아요 한 상태일 경우
                            if (FirebaseManager.cancelLikePost(
                                    post.postId,
                                    requireContext().getPaeParo().userId
                                ).isSuccess
                            ) {
                                binding.ivPostLike.setImageResource(R.drawable.ic_like_empty)
                                post.likes -= 1
                                binding.tvPostLikeCount.text = post.likes.toString()
                                liked = false
                            }
                        }
                    }
                }
            }
        })

        binding.tvPostTags.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(
                v: View?,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
                oldLeft: Int,
                oldTop: Int,
                oldRight: Int,
                oldBottom: Int
            ) {
                if (bottom != oldBottom) {
                    binding.tvPostTags.visibility = View.INVISIBLE
                    binding.tvPostTags.removeOnLayoutChangeListener(this)
                    val tagsList = post.tags.toMutableList()
                    val tagsLine1 = mutableListOf<String>()
                    val tagsLine2 = mutableListOf<String>()

                    var currentLine = tagsLine1

                    while (tagsList.isNotEmpty()) {
                        currentLine.add(tagsList.first())
                        binding.tvPostTags.text = currentLine.joinToString("   ") { "#$it" }
                        if (binding.tvPostTags.lineCount > 1) {
                            currentLine.removeAt(currentLine.size - 1)
                            currentLine = tagsLine2
                        } else {
                            tagsList.removeAt(0)
                        }
                    }

                    binding.tvPostTags.text =
                        tagsLine1.joinToString("   ") { "#$it" } + "\n" + tagsLine2.joinToString("   ") { "#$it" }
                    binding.tvPostTags.addOnLayoutChangeListener(this)
                    binding.tvPostTags.visibility = View.VISIBLE
                }
            }
        })

        // 댓글 입력 영역 Listener 추가
        binding.edtPostCommentInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                val currentDrawableRes = if (s.toString()
                        .isNotEmpty()
                ) R.drawable.ic_send else R.drawable.ic_send_disabled

                if (binding.ivPostAddComment.tag != currentDrawableRes) {
                    binding.ivPostAddComment.setImageResource(currentDrawableRes)
                    binding.ivPostAddComment.tag = currentDrawableRes
                }
            }
        })

        // 게시물 내용 확장 Listener 추가
        binding.tvPostShowAllText.setOnClickListener {
            binding.tvPostDescription.maxLines = Integer.MAX_VALUE
            binding.tvPostDescription.ellipsize = null
            binding.tvPostShowAllText.visibility = View.GONE
        }

        // 댓글 추가 버튼 Listener 추가
        binding.ivPostAddComment.setOnClickListener { view ->
            if (view.tag == R.drawable.ic_send) {
                (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                    binding.edtPostCommentInput.windowToken,
                    0
                )
                lifecycleScope.launch {
                    val comment = Comment(
                        postId = post.postId,
                        userId = requireContext().getPaeParo().userId,
                        nickname = requireContext().getPaeParo().nickname,
                        createdAt = Timestamp.now(),
                        userThumbnail = requireContext().getPaeParo().thumbnail,
                        content = binding.edtPostCommentInput.text.toString()
                    )
                    val result = FirebaseManager.createComment(
                        comment
                    )

                    if (result.isSuccess) {
                        binding.edtPostCommentInput.text.clear()
                        binding.ivPostAddComment.setImageResource(R.drawable.ic_send_disabled)
                        binding.ivPostAddComment.tag = R.drawable.ic_send_disabled

                        commentViewModel.loadComments(true)
                    }
                }
            }
        }

        // 댓글 목록 최하단 스크롤 시 다음 댓글 목록 불러오는 Listener 등록
        binding.rvPostCommentList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItem =
                    layoutManager.findLastVisibleItemPosition()

                if (layoutManager.itemCount <= (lastVisibleItem + 4) && commentAdapter.itemCount > 1) {
                    commentViewModel.loadComments(false)
                }
            }
        })

        // Gesture Detector 추가
        val gestureDetector =
            GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {

                override fun onDown(e: MotionEvent): Boolean {
                    return true
                }

                override fun onFling(
                    e1: MotionEvent,
                    e2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    var result = false
                    try {
                        val diffY = e2.y - e1.y
                        val diffX = e2.x - e1.x
                        if (abs(diffY) > abs(diffX)) {
                            if (abs(diffY) > Resources.getSystem().displayMetrics.heightPixels / 3 && abs(
                                    velocityY
                                ) > 100
                            ) {
                                if (diffY > 0) {
                                    listener?.onPostFragmentDismissed()
                                    parentFragmentManager.popBackStack()
                                }
                                result = true
                            }
                        }
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                    return result
                }
            })

        // 상단 -> 하단 스크롤 시 PostFragment 종료
        binding.root.setOnTouchListener { v, event ->
            if (gestureDetector.onTouchEvent(event)) {
                v.performClick()
                true
            } else {
                false
            }
        }
    }

    /**
     * UI 초기화
     */
    private fun setupUI() {
        // Image Slider 구성
        binding.svPostImages.setSliderAdapter(PostImageAdapter(post.images))
        binding.svPostImages.setIndicatorAnimation(IndicatorAnimationType.WORM)
        binding.svPostImages.setSliderTransformAnimation(SliderAnimations.FADETRANSFORMATION)
        binding.svPostImages.startAutoCycle()

        binding.tvPostDescription.post {
            val layout = binding.tvPostDescription.layout
            if (layout != null) {
                val lines = layout.lineCount
                if (lines > 0) {
                    if (layout.getEllipsisCount(lines - 1) > 0) {
                        binding.tvPostShowAllText.visibility = View.VISIBLE
                    } else {
                        binding.tvPostShowAllText.visibility = View.GONE
                    }
                }
            }
        }

        // UI 값 설정
        binding.ivPostLike.startAnimation(AnimationUtils.loadAnimation(context, R.anim.like_scale))
        binding.tvPostRegion.text = post.region
        binding.tvPostTitle.text = post.title
        binding.tvPostTags.text = post.tags.joinToString(separator = "   ") { "#$it" }
        binding.tvPostDescription.text = post.description
        binding.tvPostLikeCount.text = post.likes.toString()
        binding.rvPostCommentList.layoutManager = LinearLayoutManager(context)
    }

    companion object {
        fun newInstance(post: Post): PostFragment {
            val args = Bundle()
            args.putParcelable("post", post)

            val fragment = PostFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
