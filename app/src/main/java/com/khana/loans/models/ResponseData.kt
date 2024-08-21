package com.khana.loans.models


data class ResponseData constructor(var message: String, val loans: List<LoansResponse>)