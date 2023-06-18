package com.paeparo.paeparo_mobile.callback

import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.appcompat.app.AppCompatActivity
/*
장소 추가시, Map -> PlanActivty
 */
class MapResultCallBack : ActivityResultCallback<ActivityResult> {
    override fun onActivityResult(result: ActivityResult) {

        val str = when (result.resultCode) {
            AppCompatActivity.RESULT_OK -> "RESULT_OK"
            AppCompatActivity.RESULT_CANCELED -> "RESULT_CANCELED"
            else -> "RESULT_ELSE"
        }
        val str2 = result.data!!.getStringExtra("ResultData")

    }

}