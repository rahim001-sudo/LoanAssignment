package com.khana.loans.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.khana.loans.MyApplication


class ViewModelFactory(val application: MyApplication) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            AuthViewModel(application) as T
        } else if (modelClass.isAssignableFrom(LoanViewModel::class.java)) {
            LoanViewModel(application) as T
        } else if (modelClass.isAssignableFrom(ProfileUpdateModel::class.java)) {
            return ProfileUpdateModel(application) as T
        } else {
            return ProfileUpdateModel(application) as T
        }
    }
}



