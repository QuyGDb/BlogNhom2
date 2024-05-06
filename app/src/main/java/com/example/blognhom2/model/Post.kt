package com.example.blognhom2.model

import java.time.LocalDate
import java.util.Date

data class Post (
    val actor : String,
    val time : LocalDate,
    val postImg : String,
    val Title : String,
    val categories: String,
    val postContent: String
)