package com.paeparo.paeparo_mobile.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.navigation.NavigationBarView
import com.paeparo.paeparo_mobile.BuildConfig
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.fragment.HomeFragment
import com.paeparo.paeparo_mobile.fragment.MyHomeFragment
import com.paeparo.paeparo_mobile.fragment.PlanFragment
import com.paeparo.paeparo_mobile.manager.KakaoRetroFit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navbar = findViewById<NavigationBarView>(R.id.bottomNavigationView)

        // Navigation 선택 이벤트
        navbar.setOnItemSelectedListener { item ->
            val ft = supportFragmentManager.beginTransaction()
            when (item.itemId) {
                R.id.home_fragment -> {
                    ft.replace(R.id.frameLayout, HomeFragment()).commit()
                    true
                }

                R.id.community_fragment -> {
                    //ft.replace(R.id.frameLayout,MyHomeFragment()).commit()
                    true
                }

                R.id.plan_fragment -> {
                    //ft.replace(R.id.frameLayout,TripDashBoardFragment()).commit()
                    ft.replace(R.id.frameLayout, PlanFragment()).commit()
                    true
                }

                R.id.mypage_fragment -> {
                    ft.replace(R.id.frameLayout, MyHomeFragment()).commit()
                    true
                }

                else -> false
            }
        }

        navbar.selectedItemId = R.id.home_fragment
    //    testRetrofit()
    }

//    // 추후 삭제할께요 ㅎ;
//    private fun testRetrofit(){
//        val service = KakaoRetroFit.kakaoKeyWordService
//
//        CoroutineScope(Dispatchers.IO).launch {
//            val response = service.get(BuildConfig.KAKAO_API_KEY,"카카오프렌즈")
//
//            withContext(Dispatchers.Main){
//                if(response.isSuccessful){
//                                    }
//                else {
//                    Timber.d(response.code().toString()+"\n\n\n context : "+response.toString())
//                }
//            }
//        }
    }
}
