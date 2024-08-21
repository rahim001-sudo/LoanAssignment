package com.khana.loans.api

import com.khana.loans.models.Loans
import com.khana.loans.models.Photo
import com.khana.loans.models.ResponseData
import com.khana.loans.models.User
import com.khana.loans.models.UserData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("/sign_in")
    suspend fun signInUser(@Body user: User): Response<ResponseData>

    @POST("/sign_up")
    suspend fun signUpUser(@Body user: User): Response<ResponseData>

    @POST("/apply_loan")
    suspend fun applyLoan(@Body loans: Loans): Response<ResponseData>

    @GET("/loan_history")
    suspend fun loanHistory(@Query("mobile") mobile: String): Response<ResponseData>

    @POST("/upload_user_data")
    suspend fun uploadUserData(@Body userData: UserData): Response<ResponseData>

    @POST("/upload_photo")
    suspend fun uploadUserPhotos(@Body photo: Photo): Response<ResponseData>


    @POST("/update_profile")
    suspend fun updatepProfile(@Body user: User): Response<ResponseData>
}