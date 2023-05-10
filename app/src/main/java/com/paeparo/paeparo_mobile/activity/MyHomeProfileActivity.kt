package com.paeparo.paeparo_mobile.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.paeparo.paeparo_mobile.application.getPaeParo
import com.paeparo.paeparo_mobile.databinding.ActivityMyHomeProfileBinding
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import com.paeparo.paeparo_mobile.util.ImageUtil

class MyHomeProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyHomeProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyHomeProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //프로필
        ImageUtil.displayImageFromUrl(
            binding.profileImageView,
            FirebaseManager.getCurrentUserProfileUrl()
        )
        binding.profileNickname.text = getPaeParo().nickname

    }
}