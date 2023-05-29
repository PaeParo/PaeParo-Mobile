package com.paeparo.paeparo_mobile.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.paeparo.paeparo_mobile.databinding.AcitivityMapBinding

class MapActivity : AppCompatActivity() {
    lateinit var binding: AcitivityMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AcitivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mIntent = Intent(this@MapActivity, PlanActivity::class.java).apply {
            putExtra("ResultData", "DONE!")
        }
        setResult(RESULT_OK, mIntent)

        with(binding) {
            var onMap = true
            btnSearch.setOnClickListener{
                if (onMap){
                    onMap = false
                    clMap.visibility = View.INVISIBLE
                    clMapSearch.visibility = View.VISIBLE
                    svMap.isIconified = false;
                }else {
                    onMap = true
                    clMapSearch.visibility = View.INVISIBLE
                    clMap.visibility  = View.VISIBLE

                }

            }
        }
    }

}