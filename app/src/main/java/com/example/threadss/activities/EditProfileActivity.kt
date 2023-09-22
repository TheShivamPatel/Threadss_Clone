package com.example.threadss.activities

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import com.bumptech.glide.Glide
import com.example.threadss.MainActivity
import com.example.threadss.R
import com.example.threadss.databinding.ActivityEditProfileBinding
import com.example.threadss.utils.USER_NODE
import com.example.threadss.utils.USER_PROFILE_FOLDER
import com.example.threadss.utils.uploadImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var currentUser: String
    private val data = hashMapOf<String, Any>()
    private var profile: String? = null
    private lateinit var dialog: Dialog


    private lateinit var imageUri: Uri

    private var check = false

    // Registers a photo picker activity launcher in single-select mode.
    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                check = true
                imageUri = uri
                binding.profileImage.setImageURI(uri)
                Toast.makeText(this, "Image Set!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error in picking image!", Toast.LENGTH_SHORT).show()
            }
        }


    //    ===================================== MAIN ========================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // dialog box
        dialog = Dialog(this)
        dialog.setContentView(R.layout.progress_layout)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false)

        firebaseAuth = FirebaseAuth.getInstance()
        currentUser = firebaseAuth.currentUser!!.uid

        binding.profileImage.setOnClickListener {
            // Launch the photo picker and let the user choose only images.
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }


        getAllProfileDetails()

        binding.updateBtn.setOnClickListener {

            if (binding.userNameEdt.text.isEmpty()) {
                Toast.makeText(this, "Please empty field.", Toast.LENGTH_SHORT).show()
            } else {
                if (check) {
                    dialog.show()
                    updateProfilePick()
                } else {
                    dialog.show()
                    updateProfile()
                }
            }
        }

    }


    private fun getAllProfileDetails() {
        /// get my all details
        Firebase.firestore.collection(USER_NODE).document(currentUser).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val doc = task.result
                    if (doc.exists()) {
                        binding.userNameEdt.setText(doc.getString("userName"))
                        binding.profileDesc.setText(doc.getString("profileDescription"))
                        profile = doc.getString("userProfile")
                        Glide.with(this).load(profile).into(binding.profileImage)
                    }
                }

            }
    }


    private fun updateProfilePick() {
        uploadImage(imageUri, USER_PROFILE_FOLDER) {
            if (it == null) {
                Toast.makeText(this, "Error in uploading image!", Toast.LENGTH_SHORT).show()
            } else {
                try {
                    data["userProfile"] = it
                    deletePreviousProfile()
                } catch (e: Exception) {
                    Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deletePreviousProfile() {
        val ref = Firebase.storage.getReferenceFromUrl(profile.toString())
        ref.delete().addOnCompleteListener {
            Toast.makeText(this, "Image Updated to storage!", Toast.LENGTH_SHORT).show()
            updateProfile()
        }.addOnFailureListener {
            Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun updateProfile() {

        data["userName"] = binding.userNameEdt.text.toString()
        data["profileDescription"] = binding.profileDesc.text.toString()

        Firebase.firestore.collection("users")
            .document(currentUser)
            .update(data).addOnSuccessListener {
                dialog.dismiss()
                check = false
                Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show()
                getAllProfileDetails()
            }
            .addOnFailureListener {
                dialog.dismiss()
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show()
            }

    }


}