package com.example.threadss.daos

import com.example.threadss.utils.POST_NODE
import com.example.threadss.utils.USER_NODE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PostDao {

    val db = FirebaseFirestore.getInstance()
    val postCollections = db.collection(POST_NODE)
    val userCollection = db.collection(USER_NODE)
    val auth = FirebaseAuth.getInstance()



}