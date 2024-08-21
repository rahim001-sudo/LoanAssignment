package com.khana.loans.models

import com.google.gson.annotations.SerializedName
import com.khana.loans.utils.getUser


data class Loans(
    @SerializedName("loan_amount")
    var loanAmountI: Int = 0,
    var loanAmountS: String = "",
    @SerializedName("loan_duration")
    var loanDurationI: Int = 0,
    var status: String = "",
    var loanDurationS: String = "",
    @SerializedName("mobile")
    var mobile: String = getUser()!!.mobile
)

data class LoansResponse  constructor(var amount: Double?, var duration: Double?, var status: String?)