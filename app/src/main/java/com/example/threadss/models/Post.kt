package com.example.threadss.models

data class Post (
    val postedBy : String ?= " ",
    val caption: String ?= " ",
    val createdAt: Long = 0L

)