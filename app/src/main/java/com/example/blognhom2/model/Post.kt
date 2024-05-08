package com.example.blognhom2.model

import java.time.LocalDate
import java.util.Date

data class Post (
    val postID : String,
    val user : String,
    val time : LocalDate,
    val postImg : String,
    val title : String,
    val categories: String,
    val postContent: String
)