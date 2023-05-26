package com.paeparo.paeparo_mobile.fragment

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Timestamp
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.activity.MainActivity
import com.paeparo.paeparo_mobile.activity.OnPostFragmentInteractionListener
import com.paeparo.paeparo_mobile.adapter.PostImageAdapter
import com.paeparo.paeparo_mobile.application.getPaeParo
import com.paeparo.paeparo_mobile.databinding.FragmentPostBinding
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import com.paeparo.paeparo_mobile.model.Comment
import com.paeparo.paeparo_mobile.model.Post
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import kotlinx.coroutines.launch
import kotlin.math.abs

class PostFragment : Fragment() {
    private var listener: OnPostFragmentInteractionListener? = null
    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!

    private var liked = false
    private lateinit var post: Post

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        post = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("post", Post::class.java)!!
        } else {
            arguments?.getParcelable("post")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        (activity as MainActivity).onPostFragmentDisplayed()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Transition 구성
        binding.svPostImages.transitionName = "transition_image_${post.postId}"
        binding.tvPostRegion.transitionName = "transition_text_${post.postId}"

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    override fun onPause() {
        super.onPause()
        listener?.onPostFragmentDismissed()
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

        // 좋아요 버튼 Listener 추가 및 중복 처리 방지
        binding.llPostLike.setOnClickListener(object : View.OnClickListener {
            private var lastClickTime = 0L
            private val intervalMillis = 300L

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

        binding.edtPostCommentInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // 텍스트가 바뀌기 전에 호출됩니다.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // 텍스트가 바뀌는 동안에 호출됩니다.
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

        binding.ivPostAddComment.setOnClickListener { view ->
            if (view.tag == R.drawable.ic_send) {
                lifecycleScope.launch {
                    val result = FirebaseManager.createComment(
                        Comment(
                            postId = post.postId,
                            userId = requireContext().getPaeParo().userId,
                            nickname = requireContext().getPaeParo().nickname,
                            createdAt = Timestamp.now(),
                            content = binding.edtPostCommentInput.text.toString()
                        )
                    )

                    if (result.isSuccess) {
                        binding.edtPostCommentInput.text.clear()
                        binding.ivPostAddComment.setImageResource(R.drawable.ic_send_disabled)
                        binding.ivPostAddComment.tag = R.drawable.ic_send_disabled

                        // TODO: 댓글 추가 시 댓글 목록에 추가
                    }
                }
            }
        }

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

        // UI 값 설정
        binding.ivPostLike.startAnimation(AnimationUtils.loadAnimation(context, R.anim.like_scale))
        binding.tvPostRegion.text = post.region
        binding.tvPostTitle.text = post.title
        binding.tvPostTags.text = post.tags.joinToString(separator = "   ") { "#$it" }
        binding.tvPostDescription.text = post.description
        binding.tvPostLikeCount.text = post.likes.toString()
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
