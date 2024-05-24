package com.example.blognhom2.model

import java.time.LocalDate
import java.util.Date

data class Post (
    val id : String,
    val user : String,
    val time : LocalDate,
    val img : String,
    val title : String,
    val category: String,
    val content: String,
    val status: String
)