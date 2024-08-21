package com.khana.loans.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.util.Base64
import com.google.gson.Gson
import com.khana.loans.MyApplication
import com.khana.loans.R
import com.khana.loans.models.User
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

fun getUser(): User? {
    return if (AppPref.getInstance().getValue(AppPref.USER_DATA, "").isNotEmpty()) {
        Gson().fromJson(
            AppPref.getInstance().getValue(AppPref.USER_DATA, ""),
            User::class.java
        )
    } else {
        null
    }
}

 fun Context.uriTo64(imageUri: Uri): String? {
    val bytes = contentResolver.openInputStream(imageUri)!!.readBytes()
    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
    return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
}

fun isInternetOn(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities =
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
    capabilities?.let {
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || capabilities.hasTransport(
            NetworkCapabilities.TRANSPORT_WIFI
        )

    }
    return false
}

fun MyApplication.validate(user: User, isSignUp: Boolean, isUpdate: Boolean): String {
    if (user.username.trim().isEmpty()) {
        return getString(R.string.please_enter_your_username)
    }

    if (user.mobile.length != 10 && isSignUp) {
        return getString(R.string.please_enter_a_valid_number)
    }

    if (user.password.isEmpty() && !isUpdate) {
        return getString(R.string.please_enter_your_password)
    }

    if (user.newNumber.length != 10 && isUpdate) {
        return getString(R.string.please_enter_a_valid_number)
    }

    if (user.password.trim().length < 6 && !isUpdate) {
        return getString(R.string.minimum_6_characters_are_required)
    }
    return ""
}

fun convertSecondsToTime(seconds: Long): String {
    val hours = TimeUnit.SECONDS.toHours(seconds)
    val minutes = TimeUnit.SECONDS.toMinutes(seconds) % 60
    val remainingSeconds = seconds % 60

    val timeString = StringBuilder()
    if (hours > 0) {
        timeString.append(hours).append(" hour").append(if (hours > 1) "s" else "")
    }
    if (minutes > 0) {
        if (timeString.length > 0) timeString.append(", ")
        timeString.append(minutes).append(" minute").append(if (minutes > 1) "s" else "")
    }
    if (remainingSeconds > 0 || timeString.length == 0) { // Always show seconds if timeString is empty
        if (timeString.length > 0) timeString.append(", ")
        timeString.append(remainingSeconds).append(" second")
            .append(if (remainingSeconds > 1) "s" else "")
    }

    return timeString.toString()
}

