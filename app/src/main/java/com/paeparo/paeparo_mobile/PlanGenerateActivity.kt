package com.paeparo.paeparo_mobile

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Pair
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class PlanGenerateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan_generate)
        val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select dates")
                .setSelection(
                    Pair(
                        MaterialDatePicker.thisMonthInUtcMilliseconds(),
                        MaterialDatePicker.todayInUtcMilliseconds()
                    )
                )
                .build()

        dateRangePicker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance()

            calendar.timeInMillis = it.first.toLong()
            val format = SimpleDateFormat("yyyy년 MM월 dd일").format(calendar.time)
            Log.d("FIRST RANGE",format)
        }


        dateRangePicker.show(supportFragmentManager, "date_picker")
    }
}
