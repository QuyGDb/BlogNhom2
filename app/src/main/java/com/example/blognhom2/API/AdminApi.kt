package com.example.blognhom2.API

import com.example.blognhom2.model.PostInfo
import com.example.blognhom2.model.ResponseFormat
import retrofit2.Call
import retrofit2.http.*

interface AdminApi {
    //    search posts
    @GET("/admin/posts/search")
    fun searchPosts(@Query("page") page: Int, @Query("l") letter: String): Call<List<PostInfo>>
    //get posts
    @GET("/admin/posts")
    fun getPosts(@Query("page") page: Int): Call<List<PostInfo>>
    //    create post
    @PUT("/admin/posts")
    fun createPost(@Body post: PostInfo): Call<ResponseFormat>

    //    update post
    @POST("/admin/posts")
    fun updatePost(@Body post: PostInfo): Call<ResponseFormat>

    //get posts
    @GET("/admin/posts/pending")
    fun getPendingPosts(@Query("page") page: Int): Call<List<PostInfo>>

    //get posts
    @GET("/admin/posts/successful")
    fun getSuccessfulPosts(@Query("page") page: Int): Call<List<PostInfo>>

    //get posts
    @GET("/admin/posts/canceled")
    fun getCanceledPosts(@Query("page") page: Int): Call<List<PostInfo>>

    //    confirm post
    @POST("/admin/posts/confirm")
    fun confirmPendingPost(@Body post: PostInfo): Call<ResponseFormat>
    //    cancel post
    @POST("/admin/posts/cancel")
    fun cancelPendingPost(@Body post: PostInfo): Call<ResponseFormat>
}