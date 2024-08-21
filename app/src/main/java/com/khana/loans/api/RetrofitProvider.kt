package com.khana.loans.api

import android.content.Context
import android.net.ConnectivityManager
import android.net.http.NetworkException
import android.util.Log
import com.google.gson.Gson
import com.khana.loans.R
import com.khana.loans.models.ResponseData
import com.khana.loans.utils.BASE_URL
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.internal.http2.ConnectionShutdownException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


class RetrofitProvider {

    companion object {
        private lateinit var instance: Retrofit
        fun getInstance(context: Context): Retrofit {
            if (!this::instance.isInitialized) {
                val httpClient = OkHttpClient.Builder()
                    .addInterceptor(CustomInterceptor(context))
                    .build()

                instance = Retrofit.Builder().baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()).client(httpClient).build()
            }
            return instance
        }
    }

}

class CustomInterceptor(val context: Context) : Interceptor {
    @Throws(Exception::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        try {
            val response = chain.proceed(request)

            val bodyString = response.body!!.string()

            return response.newBuilder()
                .body(ResponseBody.create(response.body?.contentType(), bodyString))
                .build()
        } catch (e: Exception) {
            e.printStackTrace()
            val responseData = ResponseData("", mutableListOf())
            when (e) {
                is SocketTimeoutException -> {
                    responseData.message =
                        context.getString(R.string.timeout_please_check_your_internet_connection)
                }

                is UnknownHostException,
                is ConnectException -> {
                    responseData.message =
                        context.getString(R.string.unable_to_make_a_connection_please_check_your_internet)
                }

                is ConnectionShutdownException -> {
                    responseData.message =
                        context.getString(R.string.connection_shutdown_please_check_your_internet)
                }

                is IOException -> {
                    responseData.message =
                        context.getString(R.string.server_is_unreachable_please_try_again_later)
                }

                is IllegalStateException -> {
                    responseData.message = "${e.message}"
                }

                else -> {
                    responseData.message = "${e.message}"
                }
            }

            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(999)
                .message(Gson().toJson(responseData))
                .body(Gson().toJson(responseData).toResponseBody()).build()
        }
    }
}


