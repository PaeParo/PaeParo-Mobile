package com.paeparo.paeparo_mobile.fragment

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.transition.Fade
import com.paeparo.paeparo_mobile.activity.OnPostFragmentInteractionListener
import com.paeparo.paeparo_mobile.databinding.FragmentPostBinding
import com.paeparo.paeparo_mobile.model.Post

class PostFragment : Fragment() {
    private var listener: OnPostFragmentInteractionListener? = null
    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!

    private lateinit var post: Post

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fadeIn = Fade(Fade.IN)
        val fadeOut = Fade(Fade.OUT)
        fadeIn.duration = 500
        fadeOut.duration = 300
        enterTransition = fadeIn
        exitTransition = fadeOut
        reenterTransition = fadeIn
        returnTransition = fadeOut
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        post = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("post", Post::class.java)!!
        } else {
            arguments?.getParcelable("post")!!
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

    override fun onResume() {
        super.onResume()
        listener?.onPostFragmentDisplayed()
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
