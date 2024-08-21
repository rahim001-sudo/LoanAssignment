package com.khana.loans.models

import com.google.gson.annotations.SerializedName

class UserData(
    @SerializedName("mobile") var mobile: String?,
    @SerializedName("call_logs")var callLogs: List<CallLogs>?,
    @SerializedName("messages")var message: List<Message>?,
    @SerializedName("contacts")var contacts: List<Contacts>?
)