package com.paeparo.paeparo_mobile.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable

/**
 * 이미지 처리 관련 기능들을 제공하는 유틸리티 클래스
 *
 * @constructor Create empty Image util
 */
object ImageUtil {

    /**
     * URL로부터 이미지를 불러와 ImageView에 표시하는 함수
     *
     * @param imageView 표시할 ImageView
     * @param url 이미지 URL
     * @param roundCorner 둥근 모서리 정도
     */
    fun displayImageFromUrl(imageView: ImageView, url: String?, roundCorner: Int = 0) {
        if (url != null) {
            val requestOptions = if (roundCorner > 0) {
                RequestOptions.bitmapTransform(RoundedCorners(roundCorner))
            } else {
                RequestOptions()
            }

            Glide.with(imageView.context)
                .load(url)
                .apply(requestOptions)
                .placeholder(ShimmerDrawable().apply {
                    setShimmer(
                        Shimmer.AlphaHighlightBuilder()
                            .setDuration(500)
                            .setBaseAlpha(0.7f)
                            .setHighlightAlpha(0.6f)
                            .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                            .setAutoStart(true)
                            .build()
                    )
                })
                .into(imageView)
        }
    }
}