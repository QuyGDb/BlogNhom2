package com.example.blognhom2.API

import com.example.blognhom2.model.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface PostApi {

//    categories
    @GET("posts/categories")
    fun getCategories(): Call<List<Category>>
//    search posts
    @GET("posts/search")
    fun searchPosts(@Query("page") page: Int, @Query("l") letter: String): Call<List<PostInfo>>
    @GET("posts/search/{category}")
    fun searchPostsByCategory(@Path("category") category: String, @Query("page") page: Int, @Query("l") letter: String): Call<List<PostInfo>>
//get posts
    @GET("posts")
    fun getPosts(@Query("page") page: Int): Call<List<PostInfo>>
//get posts by category
    @GET("/posts/category/{category}")
    fun getPostsByCategory(@Path("category") category: String, @Query("page") page: Int): Call<List<PostInfo>>

    @Multipart
    @POST("/posts/upload")
    fun uploadImage(@Part file: MultipartBody.Part): Call<FileFormat>
}