package com.example.blognhom2.API

import com.example.blognhom2.model.*
import retrofit2.Call
import retrofit2.http.*

interface BlogOwnerApi {
    //    user
    @GET("/blogOwner/user")
    fun getUser(): Call<UserAuthentication>
    //    bookmarks
    @GET("/blogOwner/bookmarks/isInBookmark")
    fun isInBookmark(@Query("postID") postID: Int): Call<ResponseFormat>
    @GET("/blogOwner/bookmarks/posts")
    fun getPostsInBookmarks(@Query("page") page: Int): Call<List<PostInfo>>

    @POST("/blogOwner/bookmarks/posts")
    fun addPostToBookmarks(@Body post: PostInfo): Call<ResponseFormat>

    @DELETE("/blogOwner/bookmarks/posts")
    fun removePostFromBookmarks(@Query("postID") postID: Int): Call<ResponseFormat>

    //    search posts
    @GET("/blogOwner/posts/search")
    fun searchPosts(@Query("page") page: Int, @Query("l") letter: String): Call<List<PostInfo>>
    //get posts
    @GET("/blogOwner/posts")
    fun getPostsByBookMark(@Query("page") page: Int): Call<List<PostInfo>>
    //get posts by category
    @GET("/blogOwner/posts/category/{category}")
    fun getPostsByCategory(@Path("category") category: String, @Query("page") page: Int): Call<List<PostInfo>>
    //    get posts
    @GET("/blogOwner/posts")
    fun getPosts(@Query("page") page: Int): Call<List<PostInfo>>
    //    create post
    @PUT("/blogOwner/posts")
    fun createPost(@Body post: MyPost): Call<ResponseFormat>

    //    update post
    @POST("/blogOwner/posts")
    fun updatePost(@Body post: PostInfo): Call<ResponseFormat>

}