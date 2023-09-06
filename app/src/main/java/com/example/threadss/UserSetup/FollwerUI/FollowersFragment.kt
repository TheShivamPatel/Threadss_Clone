package com.example.threadss.UserSetup.FollwerUI

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.threadss.R
import com.example.threadss.adapter.FollowAdapter
import com.example.threadss.daos.PostDao
import com.example.threadss.databinding.FragmentFollowersBinding
import com.example.threadss.databinding.FragmentFollowingBinding
import com.example.threadss.models.User
import com.example.threadss.utils.USER_NODE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FollowersFragment : Fragment() {


    private var _binding: FragmentFollowersBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var dialog: Dialog
    private lateinit var postDao: PostDao

    private lateinit var followers: ArrayList<*>

    //    =========================================== MAIN =======================================
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFollowersBinding.inflate(inflater, container, false)
        postDao = PostDao()

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

        val list : ArrayList<String> = arrayListOf()

        firebaseAuth = FirebaseAuth.getInstance()
        postDao.userCollection.document(firebaseAuth.currentUser!!.uid).get()
            .addOnCompleteListener { task ->
                list.clear()
                if (task.isSuccessful) {
                    val doc = task.result
                    if (doc.exists()) {
                        followers = doc.get("followers") as ArrayList<*>
                        for (i in followers){
                            list.add(i.toString())
                        }

                        binding.followerRv.adapter = FollowAdapter(requireContext(), list)
                    }
                }

            }
        binding.followerRv.layoutManager = LinearLayoutManager(context)
        dialog.dismiss()

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}