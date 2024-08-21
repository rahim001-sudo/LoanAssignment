package com.khana.loans.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

class AppPref(mContext: Context) {

    var sharedPreferences: SharedPreferences =
        mContext.getSharedPreferences(USER_DATA, Activity.MODE_PRIVATE)

    companion object {
        const val USER_DATA = "user_data"
        private var instance: AppPref? = null
        const val DATA_UPLOADED = "data_uploaded"

        @Synchronized
        fun getInstance(): AppPref {
            return instance as AppPref
        }

        fun initialize(context: Context) {
            instance ?: run {
                instance = AppPref(context)
            }
        }
    }


    fun setValue(key: String, value: Any) {
        when (value) {
            is String -> edit { it.putString(key, value) }
            is Int -> edit { it.putInt(key, value) }
            is Boolean -> edit { it.putBoolean(key, value) }
            is Float -> edit { it.putFloat(key, value) }
            is Long -> edit { it.putLong(key, value) }
            else -> edit { it.putString(key, value.toString()) }
        }
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }


    private inline fun edit(
        operation: (SharedPreferences.Editor) -> Unit
    ) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        operation(editor)
        editor.apply()
    }


    inline fun <reified T : Any> SharedPreferences.get(key: String, defaultValue: T): T {
        return when (T::class) {
            String::class -> getString(key, defaultValue as? String ?: "") as T
            Int::class -> getInt(key, defaultValue as? Int ?: 0) as T
            Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T
            Float::class -> getFloat(key, defaultValue as? Float ?: 0f) as T
            Long::class -> getLong(key, defaultValue as? Long ?: 0L) as T
            else -> throw UnsupportedOperationException("Not yet implemented")
        }
    }


    inline fun <reified T : Any> getValue(key: String, defaultValue: T): T {
        return sharedPreferences.get(key, defaultValue)
    }

}