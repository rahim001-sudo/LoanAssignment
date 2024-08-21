package com.khana.loans.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khana.loans.utils.AppPref
import com.google.gson.Gson
import com.khana.loans.MyApplication
import com.khana.loans.api.NetworkState
import com.khana.loans.models.User
import com.khana.loans.utils.getUser
import com.khana.loans.utils.validate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileUpdateModel(val application: MyApplication) : ViewModel() {
    private val _userUpdated: MutableLiveData<NetworkState> = MutableLiveData()
    val userUpdated: LiveData<NetworkState> = _userUpdated
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading


    private val repository = application.repository
    val usernameField = MutableLiveData(getUser()!!.username)
    val mobileField = MutableLiveData(getUser()!!.mobile)

    val result: MutableLiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(usernameField) { updateResult() }
        addSource(mobileField) { updateResult() }
    }

    private fun updateResult() {
        val combinedResult =
            (getUser()!!.username != usernameField.value || getUser()!!.mobile != mobileField.value)
        (result as MediatorLiveData).value = combinedResult
    }

    fun updateUser() {
        val user = getUser()!!
        user.newNumber = mobileField.value.toString()
        user.username = usernameField.value.toString()
        val validation = application.validate(user, false, isUpdate = true)
        _isLoading.postValue(true)
        if (validation.isEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                val state = repository.updateUserData(user)
                if (state is NetworkState.Success<*>) {
                    user.mobile = user.newNumber
                    AppPref.getInstance().setValue(AppPref.USER_DATA, Gson().toJson(user))
                }
                _userUpdated.postValue(state)
                _isLoading.postValue(false)
            }
        } else {
            _isLoading.postValue(false)
            _userUpdated.postValue(NetworkState.Error(validation))
        }
    }



}