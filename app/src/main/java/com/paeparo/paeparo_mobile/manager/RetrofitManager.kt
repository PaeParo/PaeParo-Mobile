package com.paeparo.paeparo_mobile.manager

import com.paeparo.paeparo_mobile.BuildConfig
import com.paeparo.paeparo_mobile.model.KakaoMapModel.KaKaoResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/**
 * 키워드로 장소 검색하기
 *
 *
 */
interface KakaoKeyWordService {
    @GET("/v2/local/search/keyword.json")

    /**
     * 질의어에 매칭된 장소 검색 결과를 지정된 정렬 기준에 따라 제공합니다. 현재 위치 좌표, 반경 제한, 정렬 옵션, 페이징 등의 기능을 통해 원하는 결과를 요청 할 수 있습니다.
     *
     * @param key 카카오 API 키 ( 기본값 존재 )
     * @param query 검색을 원하는 질의어
     * @return Test
     */
    suspend fun get(
        @Header("Authorization") key : String = BuildConfig.KAKAO_API_KEY,
        @Query("query") query : String,
    ): Response<KaKaoResponse>
}


//interface KakaoCategoryService {
//    @GET("/v2/local/search/category.json")
//
//    suspend fun get(
//        @Header("Authorization") key : String,
//        @Query("query") query : String,
//    ): Response<Unit>
//}
object KakaoRetroFit {
    private const val Base_URL = "https://dapi.kakao.com"

    // LOGGING
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply{
                level = HttpLoggingInterceptor.Level.BODY
            }).build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .baseUrl(Base_URL)
            .build()
    }

    val kakaoKeyWordService: KakaoKeyWordService by lazy {
        retrofit.create(KakaoKeyWordService::class.java)
    }
}
