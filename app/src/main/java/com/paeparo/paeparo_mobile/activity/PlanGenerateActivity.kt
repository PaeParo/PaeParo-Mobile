package com.paeparo.paeparo_mobile.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.adapter.PlanGenerateAdapter
import com.paeparo.paeparo_mobile.databinding.ActivityPlanGenerateBinding

class PlanGenerateActivity : AppCompatActivity() {
    private lateinit var binding : ActivityPlanGenerateBinding
    lateinit var viewPager : ViewPager2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlanGenerateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPager = binding.vpPlanGenerate

        viewPager.apply {
            adapter = PlanGenerateAdapter(context as FragmentActivity)
            isUserInputEnabled = false
        }

    }

    override fun onBackPressed() {
        val num = viewPager.currentItem
        Toast.makeText(applicationContext,"$num",Toast.LENGTH_SHORT).show()
        viewPager.currentItem-=1
        if(num==0) finish()
        }
    fun testDataPrint(tag : String) : String{
      return intent.getStringExtra(tag)!!
    }
    fun settestData(tag : String, obj: String){
        if(intent==null) intent = Intent()
        intent.putExtra(tag,obj)
    }
//    fun nextPage(currentpage : Int){
//        viewPager.setCurrentItem(++currentpage, true)
//    }

}


