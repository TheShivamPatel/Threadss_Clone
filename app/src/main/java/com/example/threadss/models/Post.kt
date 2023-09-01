package com.example.threadss.models

data class Post (
    val postDocID : String ?= " ",
    val postedBy : String ?= " ",
    val caption: String ?= " ",
    val createdAt: Long = 0L,
    val likeBy : ArrayList<String> = ArrayList(),
    val image : String ?= " ",

)