package com.paeparo.paeparo_mobile.fragment

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.activity.MainActivity
import com.paeparo.paeparo_mobile.activity.OnPostFragmentInteractionListener
import com.paeparo.paeparo_mobile.adapter.PostImageAdapter
import com.paeparo.paeparo_mobile.application.getPaeParo
import com.paeparo.paeparo_mobile.databinding.FragmentPostBinding
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import com.paeparo.paeparo_mobile.model.Post
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import kotlinx.coroutines.launch

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

        // Image Slider 구성
        binding.svPostImages.setSliderAdapter(PostImageAdapter(post.images))
        binding.svPostImages.setIndicatorAnimation(IndicatorAnimationType.WORM)
        binding.svPostImages.setSliderTransformAnimation(SliderAnimations.FADETRANSFORMATION)
        binding.svPostImages.startAutoCycle()

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

        binding.ivPostLike.startAnimation(AnimationUtils.loadAnimation(context, R.anim.like_scale))
        binding.tvPostRegion.text = post.region
        binding.tvPostTitle.text = post.title
        binding.tvPostDescription.text = post.description
        binding.tvPostLikeCount.text = post.likes.toString()

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
