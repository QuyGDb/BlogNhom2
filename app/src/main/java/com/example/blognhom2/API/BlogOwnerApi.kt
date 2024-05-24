package com.example.blognhom2.API

import com.example.blognhom2.model.PostInfo
import com.example.blognhom2.model.ResponseFormat
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface BlogOwnerApi {
//    bookmarks
    @GET("/blogOwner/bookmarks/posts")
    fun getPostsInBookmarks(@Query("page") page: Int): Call<PostInfo>

    @POST("/blogOwner/bookmarks/posts")
    fun addPostToBookmarks(@Body post: PostInfo): Call<ResponseFormat>

    @DELETE("/blogOwner/bookmarks/posts")
    fun removePostFromBookmarks(@Query("postID") postID: Int): Call<ResponseFormat>
}