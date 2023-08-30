package com.example.threadss.UserSetup.SetUpFragments

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.example.threadss.R
import com.example.threadss.daos.UserDao
import com.example.threadss.databinding.FragmentRegistrationBinding
import com.example.threadss.models.User
import com.example.threadss.utils.USER_PROFILE_FOLDER
import com.example.threadss.utils.uploadImage
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!


    private lateinit var dialog: Dialog

    private lateinit var auth: FirebaseAuth
    private lateinit var imageUri: Uri
    private lateinit var imageUrl: String

    // Registers a photo picker activity launcher in single-select mode.
    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                imageUri = uri
                binding.profileImage.setImageURI(uri)
                Toast.makeText(activity, "Image Set!", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(activity, "Error in picking image!", Toast.LENGTH_SHORT).show()
            }
        }


    //    ============================= MAIN ============================
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)

        // dialog box
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.progress_layout)
        dialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false)




        binding.profileImage.setOnClickListener {
            // Launch the photo picker and let the user choose only images.
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }


        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        binding.registerBtn.setOnClickListener {

            val name = binding.userNameEdt.text.toString().trim()
            val email = binding.emailEdt.text.toString().trim()
            val pass = binding.passwordEdt.text.toString().trim()
            val verifyPass = binding.confirmPasswordEdt.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || verifyPass.isEmpty() || imageUri == null) {
                Toast.makeText(activity, "Please fill all requirements", Toast.LENGTH_SHORT).show()
            } else if (pass != verifyPass) {
                binding.confirmPasswordEdt.error = "Confirmation password must match"
            } else {

                // Register the user to firebase
                uploadImageToFirebase(name, email, pass)
            }

        }

        return binding.root
    }

    private fun uploadImageToFirebase(name: String, email: String, pass: String) {

        dialog.show()

        uploadImage(imageUri, USER_PROFILE_FOLDER) {
            if (it == null) {
                Toast.makeText(activity, "Error in uploading image!", Toast.LENGTH_SHORT).show()
            } else {
                try {
                    imageUrl = it
                    Toast.makeText(activity, "Image Uploaded to storage!", Toast.LENGTH_SHORT)
                        .show()
                    registerUser(name, email, pass)
                } catch (e: Exception) {
                    Toast.makeText(activity, e.message.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }

    }


// =======================================================================================
//    =========================================================================================================

    private fun registerUser(name: String, email: String, pass: String) {

        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(OnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser

                    // Verification mail sent
                    val addUser = User(user!!.uid, name, imageUrl)
                    val dao = UserDao()
                    dao.addUser(addUser)

                    Toast.makeText(context, "Registered Successfully", Toast.LENGTH_SHORT).show()
//                    findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)

                    sendEmailVerification(user)

                } else {
                    Toast.makeText(context, task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            })

    }

    private fun sendEmailVerification(user: FirebaseUser?) {

        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(OnCompleteListener {
                dialog.dismiss()
                Toast.makeText(
                    context,
                    "Verification Email is sent, Verify and Log In Again",
                    Toast.LENGTH_SHORT
                ).show()

                auth.signOut()
                findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)
            })

        } else {
            dialog.dismiss()
            Toast.makeText(context, "Failed to Send verification Email ", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
