package com.example.blognhom2.model

data class UserAuthentication(
    val id: Int,
    val username: String,
    val roles: List<String>
)