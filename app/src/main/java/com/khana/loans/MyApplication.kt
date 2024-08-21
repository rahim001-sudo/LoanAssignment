package com.khana.loans

import android.app.Application
import com.khana.loans.api.ApiService
import com.khana.loans.api.RetrofitProvider
import com.khana.loans.repository.Repository
import com.khana.loans.utils.AppPref


class MyApplication : Application() {
    lateinit var repository: Repository

    override fun onCreate() {
        super.onCreate()
        repository = Repository(
            RetrofitProvider.getInstance(applicationContext).create(ApiService::class.java),
            application = this
        )
        AppPref.initialize(this)
    }

}