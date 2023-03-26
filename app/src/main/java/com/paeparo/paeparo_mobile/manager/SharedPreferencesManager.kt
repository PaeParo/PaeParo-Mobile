package com.paeparo.paeparo_mobile.manager

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.paeparo.paeparo_mobile.application.PaeParo
import com.paeparo.paeparo_mobile.constant.SharedPreferencesKey

class SharedPreferencesManager private constructor(context: Context) {

    companion object {
        private const val PAEPARO_PREF = "paeparo_prefs"

        fun getInstance(context: Context): SharedPreferencesManager {
            return PaeParo.sharedPreferencesManager
                ?: synchronized(this) {
                    PaeParo.sharedPreferencesManager
                        ?: SharedPreferencesManager(context).also {
                            PaeParo.sharedPreferencesManager = it
                        }
                }
        }
    }

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PAEPARO_PREF, Context.MODE_PRIVATE)
    }

    // 인스턴스 생성 시 초기화
    init {
        initIfFirstLaunch()
    }

    // 앱이 처음 실행일 경우 값 초기화
    fun initIfFirstLaunch() {
        val isFirstLaunch = get(SharedPreferencesKey.KEY_FIRST_LAUNCH, true) ?: true
        if (isFirstLaunch) {
            // 앱이 처음 실행된 것으로 표시하고, 튜토리얼 완료 여부를 false로 초기화
            set(SharedPreferencesKey.KEY_FIRST_LAUNCH, false)
            set(SharedPreferencesKey.KEY_TUTORIAL_COMPLETE, false)
        }
    }

    // 값을 저장하는 함수
    fun set(key: String, value: Any?) {
        when (value) {
            is String? -> prefs.edit { putString(key, value) }
            is Int -> prefs.edit { putInt(key, value) }
            is Boolean -> prefs.edit { putBoolean(key, value) }
            is Float -> prefs.edit { putFloat(key, value) }
            is Long -> prefs.edit { putLong(key, value) }
            else -> throw IllegalArgumentException("This type of value cannot be saved into SharedPreferences")
        }
    }

    // 값을 가져오는 함수
    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String, defaultValue: T? = null): T? {
        return when (defaultValue) {
            is String? -> prefs.getString(key, defaultValue)
            is Int -> prefs.getInt(key, defaultValue)
            is Boolean -> prefs.getBoolean(key, defaultValue)
            is Float -> prefs.getFloat(key, defaultValue)
            is Long -> prefs.getLong(key, defaultValue)
            else -> throw IllegalArgumentException("This type of value cannot be retrieved from SharedPreferences")
        } as T?
    }

    // 값을 제거하는 함수
    fun removeValue(key: String) {
        prefs.edit { remove(key) }
    }

    // SharedPreferences를 초기화하는 함수
    fun clear() {
        prefs.edit { clear() }
    }

    // SharedPreferences에서 모든 키 값을 가져오는 함수
    fun getAllKeys(): Set<String> {
        return prefs.all.keys
    }

    // SharedPreferences에서 해당 키가 있는지 확인하는 함수
    fun containsKey(key: String): Boolean {
        return prefs.contains(key)
    }

    // Int 값을 SharedPreferences에서 증가시키는 함수
    fun incrementInt(key: String, increment: Int = 1) {
        val currentValue = get(key, 0) ?: 0
        set(key, currentValue + increment)
    }

    // Long 값을 SharedPreferences에서 증가시키는 함수
    fun incrementLong(key: String, increment: Long = 1) {
        val currentValue = get<Long>(key, 0) ?: 0
        set(key, currentValue + increment)
    }

    // Float 값을 SharedPreferences에서 증가시키는 함수
    fun incrementFloat(key: String, increment: Float = 1f) {
        val currentValue = get(key, 0f) ?: 0f
        set(key, currentValue + increment)
    }

    // Double 값을 SharedPreferences에서 증가시키는 함수
    fun incrementDouble(key: String, increment: Double = 1.0) {
        val currentValue = get(key, 0.0) ?: 0.0
        set(key, currentValue + increment)
    }

    // SharedPreferences에서 Int 값을 가져오면서 값이 없는 경우에 기본값을 설정하여 가져오는 함수
    fun getIntOrDefault(key: String, defaultValue: Int): Int {
        return get(key, defaultValue) ?: defaultValue
    }

    // SharedPreferences에서 Long 값을 가져오면서 값이 없는 경우에 기본값을 설정하여 가져오는 함수
    fun getLongOrDefault(key: String, defaultValue: Long): Long {
        return get(key, defaultValue) ?: defaultValue
    }

    // SharedPreferences에서 Float 값을 가져오면서 값이 없는 경우에 기본값을 설정하여 가져오는 함수
    fun getFloatOrDefault(key: String, defaultValue: Float): Float {
        return get(key, defaultValue) ?: defaultValue
    }

    // SharedPreferences에서 Double 값을 가져오면서 값이 없는 경우에 기본값을 설정하여 가져오는 함수
    fun getDoubleOrDefault(key: String, defaultValue: Double): Double {
        return get(key, defaultValue) ?: defaultValue
    }
}