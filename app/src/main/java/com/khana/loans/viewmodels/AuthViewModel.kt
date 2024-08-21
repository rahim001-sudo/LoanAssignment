package com.khana.loans.viewmodels

import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khana.loans.MyApplication
import com.khana.loans.R
import com.khana.loans.api.NetworkState
import com.khana.loans.models.User
import com.khana.loans.utils.validate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel(val application: MyApplication) : ViewModel() {

    private val repository = application.repository


    private val _userSignIn: MutableLiveData<NetworkState> = MutableLiveData()
    val userSignIn: LiveData<NetworkState> = _userSignIn

    private val _userSignUp: MutableLiveData<NetworkState> = MutableLiveData()
    val userSignUp: LiveData<NetworkState> = _userSignUp

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading


    fun signIn(user: User) {
        val validation = application.validate(user, false,false)
        _isLoading.postValue(true)
        if (validation.isEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                val state = repository.signIn(user)
                _userSignIn.postValue(state)
                _isLoading.postValue(false)
            }
        } else {
            _isLoading.postValue(false)
            _userSignIn.postValue(NetworkState.Error(validation))
        }
    }


    fun signUp(user: User) {
        val validation = application.validate(user, true,false)
        _isLoading.postValue(true)
        if (validation.isEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                val state = repository.signUp(user)
                _userSignUp.postValue(state)
                _isLoading.postValue(false)
            }
        } else {
            _isLoading.postValue(false)
            _userSignUp.postValue(NetworkState.Error(validation))
        }
    }




}