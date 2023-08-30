package com.example.threadss.utils

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

fun uploadImage(uri : Uri, folderName: String, callback:(String?)-> Unit) {

    var imageUrl :String ?= null

    //for generation random UID
    val fileName = UUID.randomUUID().toString() + ".jpg"

    FirebaseStorage.getInstance().getReference(folderName).child(fileName)
        .putFile(uri)
        .addOnSuccessListener {
            it.storage.downloadUrl.addOnSuccessListener {
                imageUrl = it.toString()
                callback(imageUrl)
            }
        }
}