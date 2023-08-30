package com.example.threadss.UserSetup.mainfragments

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.threadss.R
import com.example.threadss.databinding.FragmentCreateThreadBinding
import com.example.threadss.models.Post
import com.example.threadss.utils.IMAGE_HOLDER
import com.example.threadss.utils.POST_IMAGE_FOLDER
import com.example.threadss.utils.POST_NODE
import com.example.threadss.utils.USER_PROFILE_FOLDER
import com.example.threadss.utils.uploadImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class CreateThreadFragment : Fragment() {


    private var _binding: FragmentCreateThreadBinding? = null
    private val binding get() = _binding!!

    private lateinit var dialog: Dialog


    private val auth = FirebaseAuth.getInstance()
    private val currentUserId = auth.currentUser!!.uid

    // getting current time
    private val currentTime = System.currentTimeMillis()


    private lateinit var imageUri: Uri
    private lateinit var imageUrl: String

    // Post image picker activity launcher in single-select mode.
    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                imageUri = uri
                binding.postImg.setImageURI(uri)
                binding.postImg.visibility = View.VISIBLE
                Toast.makeText(activity, "Image Set!", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(activity, "Error in picking image!", Toast.LENGTH_SHORT).show()
            }
        }


//    ========================================================== MAIN ============================/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCreateThreadBinding.inflate(inflater, container, false)

        // dialog box
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.progress_layout)
        dialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false)


        // select a image for post
        binding.attachBtn.setOnClickListener {
            // Launch the photo picker and let the user choose only images.
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }


        binding.postBtn.setOnClickListener {

            if (binding.postDesc.text.toString().isEmpty()) {
                Toast.makeText(activity, "Please write something!", Toast.LENGTH_SHORT).show()
            } else {
                postToFirebase()
            }

        }


        return binding.root
    }

//
//    private fun uploadImageToFirebase() {
//
//        uploadImage(imageUri, POST_IMAGE_FOLDER) {
//            if (it == null) {
//                Toast.makeText(activity, "Error in uploading image!", Toast.LENGTH_SHORT).show()
//            } else {
//                try {
//                    imageUrl = it
//                    Toast.makeText(activity, "Image Uploaded to storage!", Toast.LENGTH_SHORT)
//                        .show()
//                    postToFirebase(imageUrl)
//                } catch (e: Exception) {
//                    Toast.makeText(activity, e.message.toString(), Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//
//    }


    // posting post to firebase store
    private fun postToFirebase() {

        dialog.show()

        val db = Firebase.firestore


        val post = hashMapOf<String, Any>(
            "postedBy" to currentUserId,
            "caption" to binding.postDesc.text.toString().trim(),
            "createdAt" to currentTime,
        )

        db.collection(POST_NODE).add(post).addOnSuccessListener {

            binding.postDesc.text = null

            Toast.makeText(requireContext(), "Post added.", Toast.LENGTH_SHORT).show()

            dialog.dismiss()
        }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error in uploading post.", Toast.LENGTH_SHORT)
                    .show()
                dialog.dismiss()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}