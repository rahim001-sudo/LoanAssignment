package com.khana.loans.models

import com.google.gson.annotations.SerializedName

data class User (
    var username: String = "",
    var mobile: String = "",
    var password: String = "",
    @SerializedName("new_mobile") var newNumber: String=""
)

