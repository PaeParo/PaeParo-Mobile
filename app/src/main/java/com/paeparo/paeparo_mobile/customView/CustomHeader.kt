package com.paeparo.paeparo_mobile.customView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.paeparo.paeparo_mobile.R
import com.paeparo.paeparo_mobile.databinding.CustomHeaderBinding

class CustomHeader @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null, // XML에서 생성할 때, 필요
    defStyleAttr: Int = 0, // ThemeStyle과 함께 뷰 생성할 때, 필요
) : LinearLayout(context, attrs, defStyleAttr) {
    private val binding = CustomHeaderBinding.inflate(LayoutInflater.from(context), this, true)
    val tv_title get() = binding.title
    val tv_subtitle get() = binding.subtitle

    init {
        // attrs 가 null 이 아니라면
        attrs?.let {
            // 속성값을 가져와서 변수에 저장합니다.
            val typedArray = context.obtainStyledAttributes(it, R.styleable.CustomHeader)

            // 가져온 속성값을 변수에 저장합니다.
            val title = typedArray.getString(R.styleable.CustomHeader_title)
            val subtitle = typedArray.getString(R.styleable.CustomHeader_subtitle)

            // 속성값이 존재한다면 TextView 에 적용합니다.
            if (title != null) {
                tv_title.text = title
            }

            if (subtitle != null) {
                tv_subtitle.text = subtitle
            }
            // TypedArray 를 recycle 합니다.
            typedArray.recycle()
        }

    }
}
