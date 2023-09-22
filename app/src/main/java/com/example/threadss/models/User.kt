package com.example.threadss.models


data class User(
    val userId: String = "",
    val userName: String = "",
    val userProfile: String? = "",
    val profileDescription : String?= "",
    val followers : ArrayList<String> = ArrayList(),
    val following : ArrayList<String> = ArrayList()
)