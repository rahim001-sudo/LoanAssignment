package com.khana.loans.api

sealed class NetworkState {
    class Error(val message: String): NetworkState()
    class Success<out T>(val data: T): NetworkState()
}