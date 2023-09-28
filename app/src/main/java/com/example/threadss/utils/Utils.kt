package com.example.threadss.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.exifinterface.media.ExifInterface
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.UUID

fun uploadImage(uri: Uri, folderName: String, callback: (String?) -> Unit) {

    var imageUrl: String? = null
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

fun uploadImageToFirebaseCompres(
    imageUri: Uri,
    contentResolver: ContentResolver,
    context: Context,
    folderName: String,
    callback: (String?) -> Unit
) {

    /// ++++ jfjsd faf++++ dfsj fjjfk++++++ kdjflk++++++ kfjsalf j
    var bitmap: Bitmap? = null
    try {
        bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
        // Check and correct the image orientation
        val exif = ExifInterface(contentResolver.openInputStream(imageUri)!!)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        val rotation = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }

        if (rotation != 0) {
            val matrix = Matrix()
            matrix.postRotate(rotation.toFloat())
            bitmap =
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }


    } catch (e: Exception) {
        Toast.makeText(context, "Error! , ${e.message.toString()}", Toast.LENGTH_SHORT).show()
    }
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap?.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream)
    val data = byteArrayOutputStream.toByteArray()

    //for generation random UID


    var imageUrl: String? = null
    //for generation random UID
    val fileName = UUID.randomUUID().toString() + ".jpg"

    FirebaseStorage.getInstance().getReference(folderName).child(fileName)
        .putBytes(data)
        .addOnSuccessListener {
            it.storage.downloadUrl.addOnSuccessListener {
                imageUrl = it.toString()
                callback(imageUrl)
            }
        }
        .addOnFailureListener {
            Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show()
        }

}
