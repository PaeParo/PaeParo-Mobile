package com.paeparo.paeparo_mobile.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.paeparo.paeparo_mobile.TestMyHomeComment
import com.paeparo.paeparo_mobile.adapter.MyHomeCommentAdapter
import com.paeparo.paeparo_mobile.application.getPaeParo
import com.paeparo.paeparo_mobile.databinding.ActivityMyHomeCommentBinding
import com.paeparo.paeparo_mobile.manager.FirebaseManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class MyHomeCommentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyHomeCommentBinding
    private lateinit var commentAdapter:MyHomeCommentAdapter
    private  lateinit var verticalManager: LinearLayoutManager
    var datas = mutableListOf<TestMyHomeComment>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyHomeCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        commentAdapter = MyHomeCommentAdapter(datas)

        verticalManager = LinearLayoutManager(this@MyHomeCommentActivity)
        verticalManager.orientation = LinearLayoutManager.VERTICAL

        binding.myhomeCommentRecyclerview.apply{
            layoutManager = verticalManager
            adapter = commentAdapter
        }

        //작성한 댓글 가져오기
        CoroutineScope(Dispatchers.IO).launch {
            val result = FirebaseManager.getUser(getPaeParo().userId)
            val userId = if(result.isSuccess){
                result.data?.userId
            } else{
                Timber.e("null userId")
            }
            val result_comment = FirebaseManager.getUserComments(userId as String)
            val comment = if(result_comment.isSuccess){
                Timber.d("comment success : " + result_comment.data)
            }else{
                Timber.e("null comment")
            }

        }

        datas.apply{
            add(TestMyHomeComment("경주여행","이번에 경주 방문한 이유 ! 겹벚꽃보러\uD83C\uDF38\uD83C\uDF38\n" +
                    "\n" +
                    "겹벚꽃 명소라는 불국사로 갔습니다~ \n" +
                    "\n" +
                    "\u200B\n" +
                    "\n" +
                    "터미널에 짐 보관함이 있어서 짐 보관하고 \n" +
                    "\n" +
                    "불국사까지 버스타고 갔는데 \n" +
                    "\n" +
                    "은근히 버스가 많아서 가기 편했음 "))
        }

    }
}