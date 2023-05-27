package com.paeparo.paeparo_mobile.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.databinding.ActivityMainBinding
import com.paeparo.paeparo_mobile.fragment.CommunityFragment
import com.paeparo.paeparo_mobile.fragment.MyHomeFragment
import com.paeparo.paeparo_mobile.fragment.TripFragment

class MainActivity : AppCompatActivity(), OnPostFragmentInteractionListener {
    private lateinit var binding: ActivityMainBinding
    private var currentTabId: Int = -1
    private val communityFragment = CommunityFragment()
    private val tripFragment = TripFragment()
    private val myHomeFragment = MyHomeFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .add(R.id.fl_main_view, tripFragment)
            .commit()

        binding.bvMainBottomNavigation.setOnItemSelectedListener { item ->
            if (item.itemId != currentTabId) {
                val ft = supportFragmentManager.beginTransaction()
                ft.setCustomAnimations(
                    R.anim.fade_in,  // 새로운 Fragment가 들어올 때의 애니메이션
                    R.anim.fade_out   // 현재 Fragment가 나갈 때의 애니메이션
                )
                when (item.itemId) {
                    R.id.community_fragment -> {
                        ft.replace(R.id.fl_main_view, communityFragment).commit()
                    }

                    R.id.trip_fragment -> {
                        ft.replace(R.id.fl_main_view, tripFragment).commit()
                    }

                    R.id.mypage_fragment -> {
                        ft.replace(R.id.fl_main_view, myHomeFragment).commit()
                    }
                }
                currentTabId = item.itemId
                true
            } else {
                false
            }
        }

        binding.bvMainBottomNavigation.selectedItemId = R.id.trip_fragment
    }

    override fun onPostFragmentDisplayed() {
        binding.bvMainBottomNavigation.visibility = View.GONE
        val layoutParams = binding.flMainView.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        binding.flMainView.layoutParams = layoutParams
    }

    override fun onPostFragmentDismissed() {
        binding.bvMainBottomNavigation.visibility = View.VISIBLE
        val layoutParams = binding.flMainView.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.bottomToTop = binding.bvMainBottomNavigation.id
        binding.flMainView.layoutParams = layoutParams
    }
}

interface OnPostFragmentInteractionListener {
    fun onPostFragmentDisplayed()
    fun onPostFragmentDismissed()
}