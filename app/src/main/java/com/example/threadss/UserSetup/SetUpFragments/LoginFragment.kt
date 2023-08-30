package com.example.threadss.UserSetup.SetUpFragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.threadss.MainActivity
import com.example.threadss.R
import com.example.threadss.databinding.FragmentLoginBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {


    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()


        binding.userLoginBtn.setOnClickListener {

            val email = binding.userEmailEdt.text.toString().trim()
            val pass = binding.userPassEdt.text.toString().trim()

            if (email.isEmpty()) {
                binding.userEmailEdt.error = "Enter email"
            } else if (pass.isEmpty()) {
                binding.userPassEdt.error = "Enter password"
            } else {
                loginUser(email, pass)
            }

        }

        return binding.root
    }

    private fun loginUser(email: String, pass: String) {

        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(OnCompleteListener { task ->

                if (task.isSuccessful) {

                    val verify = auth.currentUser?.isEmailVerified
                    if (verify == true) {
                        startActivity(Intent(requireContext(), MainActivity::class.java))
                        activity?.finish()
                        Toast.makeText(context, "Login successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Please Verify your Email !", Toast.LENGTH_SHORT)
                            .show()
                    }

                } else {
                    Toast.makeText(context, "Error !", Toast.LENGTH_SHORT).show()
                }

            })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}