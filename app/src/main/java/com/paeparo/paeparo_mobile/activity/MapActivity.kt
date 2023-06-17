package com.paeparo.paeparo_mobile.activity

import androidx.appcompat.widget.SearchView
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.paeparo.paeparo_mobile.databinding.AcitivityMapBinding
import com.paeparo.paeparo_mobile.manager.KakaoRetroFit

class MapActivity : AppCompatActivity(), SearchView.OnQueryTextListener {
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
            svMap.setOnQueryTextListener(this@MapActivity);


            var onMap = true
            btnSearch.setOnClickListener{
                if (onMap){
                    onMap = !onMap
                    clMap.visibility = View.INVISIBLE
                    clMapSearch.visibility = View.VISIBLE
                    svMap.isIconified = false;


                }else {
                    onMap = !onMap
                    clMapSearch.visibility = View.INVISIBLE
                    clMap.visibility  = View.VISIBLE
                }

            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        KakaoRetroFit.searchWithQuery(query);
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }


}