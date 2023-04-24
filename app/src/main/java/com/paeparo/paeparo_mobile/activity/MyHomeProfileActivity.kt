package com.paeparo.paeparo_mobile.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.paeparo.paeparo_mobile.databinding.ActivityMyHomeProfileBinding

class MyHomeProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyHomeProfileBinding

    private val OPEN_GALLERY = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyHomeProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //프로필을 클릭했을 경우 갤러리로 이동
        binding.profileImageView.setOnClickListener(){
            val intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.setType("image/*")
            startActivityForResult(intent,OPEN_GALLERY)
        }
    }

    //갤러리 이동 후 선택한 이미지 가져와서 적용
    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == OPEN_GALLERY){
                val currentImageUrl : Uri? = data?.data
                try{
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,currentImageUrl)
                    binding.profileImageView.setImageBitmap(bitmap)
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        }else{
            Log.d("MyHomeProfile","MyHomeProfileFail")
        }
    }
}