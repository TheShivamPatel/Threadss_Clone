package com.example.threadss.models


data class User(
    val userId: String = "",
    val userName: String = "",
    var userProfile: String? = "",
    val followers : ArrayList<String> = ArrayList(),
    val following : ArrayList<String> = ArrayList()
)