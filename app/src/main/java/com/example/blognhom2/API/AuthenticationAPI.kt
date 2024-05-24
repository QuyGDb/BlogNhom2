package com.example.blognhom2.API

import com.example.blognhom2.model.UserAuthentication
import com.example.blognhom2.model.ResponseFormat
import com.example.blognhom2.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthenticationAPI {
    @GET("/login")
    fun login(): Call<UserAuthentication>
    @POST("/logout")
    fun logout(): Call<Void>
    @POST("/register")
    fun register(@Body user: User): Call<ResponseFormat>
}