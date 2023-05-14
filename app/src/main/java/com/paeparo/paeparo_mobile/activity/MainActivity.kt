package com.paeparo.paeparo_mobile.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.databinding.ActivityMainBinding
import com.paeparo.paeparo_mobile.fragment.InvitationFragment
import com.paeparo.paeparo_mobile.fragment.MyHomeFragment
import com.paeparo.paeparo_mobile.fragment.TripFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentTabId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Navigation 선택 이벤트
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            if (item.itemId != currentTabId) {
                val ft = supportFragmentManager.beginTransaction()
                when (item.itemId) {
                    R.id.community_fragment -> {
//                        ft.replace(R.id.frameLayout, MyHomeFragment()).commit()
                    }

                    R.id.trip_fragment -> {
                        ft.replace(R.id.frameLayout, TripFragment()).commit()
                    }

                    R.id.mypage_fragment -> {
                        ft.replace(R.id.frameLayout, MyHomeFragment()).commit()
                    }

                    R.id.invitation_fragment -> {
                        ft.replace(R.id.frameLayout, InvitationFragment()).commit()
                    }
                }
                currentTabId = item.itemId
                true
            } else {
                false
            }
        }

        binding.bottomNavigationView.selectedItemId = R.id.trip_fragment
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
