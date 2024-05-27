package com.example.blognhom2.model


data class PostInfo (
    val id : Int,
    val user : String,
    val time : String,
    val img : String,
    var title : String,
    val category: String,
    val content: String,
    val status: String
)