package com.example.threadss.UserSetup.FollwerUI

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.threadss.R
import com.example.threadss.adapter.FollowAdapter
import com.example.threadss.daos.PostDao
import com.example.threadss.databinding.FragmentFollowingBinding
import com.example.threadss.utils.USER_NODE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FollowingFragment : Fragment() {

    private var _binding: FragmentFollowingBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var dialog: Dialog
    private lateinit var postDao: PostDao

    private lateinit var following: ArrayList<*>

    //    =========================================== MAIN =======================================
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFollowingBinding.inflate(inflater, container, false)

        // dialog box
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.progress_layout)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false)


        getFollowersList()

        return binding.root
    }


    private fun getFollowersList() {

        dialog.show()

        firebaseAuth = FirebaseAuth.getInstance()
         Firebase.firestore.collection(USER_NODE).document(firebaseAuth.currentUser!!.uid).get()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    val doc = task.result
                    if (doc.exists()) {
                        following = doc.get("following") as ArrayList<*>

                        binding.followingRv.adapter = FollowAdapter(requireContext(), following)

                    }
                }
            }

        binding.followingRv.layoutManager = LinearLayoutManager(context)
        dialog.dismiss()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}