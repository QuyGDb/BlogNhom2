package com.example.blognhom2.model

import java.time.LocalDate

data class PostInfo (
    val id : Int,
    val user : String,
    val time : String,
    val img : String,
    val title : String,
    val category: String,
    val content: String,
    val status: String
)