package com.paeparo.paeparo_mobile.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.paeparo.paeparo_mobile.adapter.CommunityImagesAdapter
import com.paeparo.paeparo_mobile.adapter.CommunityTagAdapter
import com.paeparo.paeparo_mobile.databinding.ActivityCommunityWriteBinding
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import com.paeparo.paeparo_mobile.model.Post
import kotlinx.coroutines.launch


class CommunityWriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommunityWriteBinding
    private var tagAdapter = CommunityTagAdapter()
    private var pictureAdapter = CommunityImagesAdapter()

    private var OPEN_GALLERY = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunityWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val builder = AlertDialog.Builder(this) //다이어로그 창

        //태그 뷰 adapter
        binding.communityTagRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.communityTagRecyclerView.adapter = tagAdapter

        //이미지 뷰 adapter
        binding.communityPictureRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.communityPictureRecyclerView.adapter = pictureAdapter


        //상단 제어
        binding.write.setOnClickListener {
            lifecycleScope.launch {
                val post = Post(
                    title = binding.title.toString(),
                    description = binding.content.toString(),
                    createdAt = Timestamp.now(),
                    tags = tagAdapter.getTagList() as MutableList<String>,
                )
                val result = FirebaseManager.createPost(
                    post,pictureAdapter.getImageList()
                )
            }
        }
        binding.back.setOnClickListener {
            finish()
        }
        binding.community.text = "글쓰기"

        //태그 추가
        binding.cvCommunityWriteAddTag.setOnClickListener {
            val inputEditText = EditText(this@CommunityWriteActivity)
            builder.setTitle("태그 추가")
                .setView(inputEditText)
                .setPositiveButton(
                    "확인"
                ) { dialog, id ->
                    tagAdapter.addTag(inputEditText.text.toString())
                }
                .setNegativeButton("취소", null)
            builder.show()
        }

        //이미지 추가
        binding.pictureTake.setOnClickListener {
            val intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, OPEN_GALLERY)
        }
    }

    /**
     * 갤러리로부터 bitmap 형식으로 가져오는 코드
     *
     * @param requestCode 갤러리 열기 요청 코드
     * @param resultCode 갤러리 액티비티 종료 결과 코드
     * @param data 갤러리 액티비티로부터 전달받은 Intent 데이터
     */
    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == OPEN_GALLERY) {
                val currentImageUrl: Uri? = data?.data
                try {
                    pictureAdapter.addTag(currentImageUrl.toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            Log.d("onActivityResult", "GALLERY error")
        }
    }
}
