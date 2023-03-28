package com.paeparo.paeparo_mobile.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.navigation.NavigationBarView
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.fragment.MyHomeFragment
import com.paeparo.paeparo_mobile.fragment.PlanFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navbar = findViewById<NavigationBarView>(R.id.bottomNavigationView)

        // Navigation 선택 이벤트
        navbar.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item ->
            val ft = supportFragmentManager.beginTransaction()
            when(item.itemId) {
                R.id.home_fragment -> {
                    //ft.replace(R.id.frameLayout,MyHomeFragment()).commit()
                    true
                }
                R.id.community_fragment -> {
                    //ft.replace(R.id.frameLayout,MyHomeFragment()).commit()
                    true
                }
                R.id.plan_fragment -> {
                    ft.replace(R.id.frameLayout,PlanFragment()).commit()
                    true
                }
                R.id.mypage_fragment -> {
                    ft.replace(R.id.frameLayout,MyHomeFragment()).commit()
                    true
                }
                else -> false

            }
        });
    }


}
