package com.paeparo.paeparo_mobile.manager

import com.paeparo.paeparo_mobile.BuildConfig
import com.paeparo.paeparo_mobile.model.NaverResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

/**
 * 네이버 geocode를 이용한 지역 및 위도,경도 검색
 *
 *
 *
 */

interface NaverGeocode {
    @GET("/map-geocode/v2/geocode")

    suspend fun get(
        @Query("query") query: String,
        @Header("X-NCP-APIGW-API-KEY-ID") ClientID: String = BuildConfig.NAVER_CLIENT_ID,
        @Header("X-NCP-APIGW-API-KEY") ClientSecret: String = BuildConfig.NAVER_CLIENT_KEY,
    ): Response<NaverResponse>
}

object NaverRetroFit {
    private const val Base_URL = "https://naveropenapi.apigw.ntruss.com"

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
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

    val naverGeocode: NaverGeocode by lazy {
        retrofit.create(NaverGeocode::class.java)
    }

    public fun searchWithQuery(query: String?): NaverResponse? {
        if (query == null) return null
        val service = NaverRetroFit.naverGeocode
        var result: NaverResponse? = null

        val job = CoroutineScope(Dispatchers.IO).launch {
            result =
                service.get(query, BuildConfig.NAVER_CLIENT_ID, BuildConfig.NAVER_CLIENT_KEY).body()
        }

        runBlocking {
            job.join()
        }
        return result
    }


}