package com.khana.loans.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khana.loans.MyApplication
import com.khana.loans.R
import com.khana.loans.api.NetworkState
import com.khana.loans.models.Loans
import com.khana.loans.utils.getUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoanViewModel(val application: MyApplication) : ViewModel() {
    private val _loanData: MutableLiveData<NetworkState> = MutableLiveData()
    val loanData: LiveData<NetworkState> = _loanData


    private val _appliedLoans: MutableLiveData<NetworkState> = MutableLiveData()
    val appliedLoans: LiveData<NetworkState> = _appliedLoans

    private val _loading: MutableLiveData<Boolean> = MutableLiveData()
    val isLoading: LiveData<Boolean> = _loading

    private val repository = application.repository

    fun applyLoan(loans: Loans) {
        val validation = validate(loans)
        if (validation.isEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                _loading.postValue(true)
                val state = repository.applyLoan(loans)
                _loanData.postValue(state)
                _loading.postValue(false)
            }
        } else {
            _loading.postValue(false)
            _loanData.postValue(NetworkState.Error(validation))
        }
    }

    fun loanHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.postValue(true)
            val appliedLoanData = repository.loanHistory(getUser()!!.mobile)
            _appliedLoans.postValue(appliedLoanData)
            _loading.postValue(false)
        }
    }


    private fun validate(loans: Loans): String {
        if (loans.loanAmountS.isEmpty()) {
            return application.getString(R.string.please_enter_loan_amount)
        }

        if (loans.loanDurationS.isEmpty()) {
            return application.getString(R.string.please_enter_loan_duration)
        }

        loans.loanAmountI = loans.loanAmountS.toInt()
        loans.loanDurationI = loans.loanDurationS.toInt()
        return ""
    }


}