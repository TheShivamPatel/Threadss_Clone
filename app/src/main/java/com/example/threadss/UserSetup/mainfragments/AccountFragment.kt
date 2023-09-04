package com.example.threadss.UserSetup.mainfragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.threadss.R
import com.example.threadss.activities.SplashScreen
import com.example.threadss.adapter.FireAdapter
import com.example.threadss.daos.PostDao
import com.example.threadss.adapter.UserPostAdapter
import com.example.threadss.databinding.FragmentAccountBinding
import com.example.threadss.models.Post
import com.example.threadss.utils.USER_NODE
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private var name: String? = null
    private var profile: String? = null
    private lateinit var postDao: PostDao

    private lateinit var myAdapter: FireAdapter

    private lateinit var dialog: Dialog
    private lateinit var logOutDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(layoutInflater)

        // progress dialog box
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.progress_layout)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false)



        firebaseAuth = FirebaseAuth.getInstance()

        Firebase.firestore.collection(USER_NODE).document(firebaseAuth.currentUser!!.uid).get()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    val doc = task.result
                    if (doc.exists()) {
                        name = doc.getString("userName")
                        profile = doc.getString("userProfile")

                        binding.userNameTxt.text = name.toString()
                        Glide.with(requireContext()).load(profile).into(binding.userImage)
                    }
                }

            }

        getCategoryFromFirebase()

        binding.logoutBtn.setOnClickListener {
            // dialog box
            showLogoutDialog()
        }

        return binding.root
    }

    private fun getCategoryFromFirebase() {

        dialog.show()

        postDao = PostDao()
        val postsCollections = postDao.postCollections

        val list = ArrayList<Post>()

        postsCollections.get().addOnSuccessListener {
            list.clear()
            for (doc in it.documents) {

                val data = doc.toObject(Post::class.java)

                if (data!!.postedBy.equals(firebaseAuth.currentUser!!.uid)) {
                    list.add(data!!)
                }
            }
            list.sortByDescending {
                it.createdAt
            }
            binding.userPostRv.adapter = UserPostAdapter(requireContext(), list)
            binding.userPostRv.layoutManager = LinearLayoutManager(context)

            dialog.dismiss()
        }

    }


    private fun showLogoutDialog() {
        logOutDialog = Dialog(requireContext())
        logOutDialog.setContentView(R.layout.logout_layout)
        logOutDialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent);
        logOutDialog.setCancelable(false)
        logOutDialog.show()

        val logoutTxt: TextView = logOutDialog.findViewById(R.id.logout_text)
        val cancelTxt: TextView = logOutDialog.findViewById(R.id.cancel_text)

        logoutTxt.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(requireContext(), SplashScreen::class.java))
            activity?.finish()
        }

        cancelTxt.setOnClickListener {
            logOutDialog.dismiss()
        }


    }


}