package com.paeparo.paeparo_mobile.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.paeparo.paeparo_mobile.databinding.AcitivityMapBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MapActivity : AppCompatActivity() {
    lateinit var binding: AcitivityMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AcitivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mIntent = Intent(this@MapActivity,PlanActivity::class.java).apply{
            putExtra("ResultData","DONE!")
        }
        setResult(RESULT_OK,mIntent)

        CoroutineScope(Dispatchers.Main).launch {
            delay(5000)
            if(!isFinishing) finish()
        }
    }

}