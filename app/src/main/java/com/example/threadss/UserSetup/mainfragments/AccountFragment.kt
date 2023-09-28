package com.example.threadss.UserSetup.mainfragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.threadss.R
import com.example.threadss.activities.EditProfileActivity
import com.example.threadss.activities.FollowerActivity
import com.example.threadss.activities.SplashScreen
import com.example.threadss.adapter.FireAdapter
import com.example.threadss.daos.PostDao
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

    private lateinit var myAdapter : FireAdapter


    private lateinit var imageBoxDialog: Dialog
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


        // progress dialog box
        imageBoxDialog = Dialog(requireContext())
        imageBoxDialog.setContentView(R.layout.image_dialogbox)
        imageBoxDialog.window?.setBackgroundDrawableResource(android.R.color.transparent);
        imageBoxDialog.setCancelable(true)


        /// get my all details
        firebaseAuth = FirebaseAuth.getInstance()
        Firebase.firestore.collection(USER_NODE).document(firebaseAuth.currentUser!!.uid).get()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    val doc = task.result
                    if (doc.exists()) {
                        name = doc.getString("userName")
                        profile = doc.getString("userProfile")
                        val followers = doc.get("followers") as List<String>
                        val desc = doc.getString("profileDescription")

                        binding.textView8.text = "${followers.size} followers"
                        binding.userNameTxt.text = name.toString()
                        binding.textView6.text = desc
                        Glide.with(requireContext()).load(profile).into(binding.userImage)
                        val imageProfile = imageBoxDialog.findViewById<ImageView>(R.id.image_profile_link)
                        Glide.with(this).load(profile).into(imageProfile)
                    }
                }

            }


        // SHARE PROFILE BUTTON
        binding.shareProfileBtn.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "My UserId: *${name}* - _Download Threadss app to connect with me._")
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        binding.userImage.setOnClickListener {
            imageBoxDialog.show()
        }


        // EDIT PROFILE BUTTON
        binding.editProfileBtn.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }


        setRecy()

        binding.logoutBtn.setOnClickListener {
            // dialog box
            showLogoutDialog()
        }

        binding.textView8.setOnClickListener {
//            startActivity(Intent(requireContext(), FollowerActivity::class.java))
            val intent = Intent(requireContext(), FollowerActivity::class.java)
            intent.putExtra("userId", name)
            startActivity(intent)
        }

        return binding.root
    }


    private fun setRecy() {

        dialog.show()


        postDao = PostDao()
        val postsCollections = postDao.postCollections
        val query = postsCollections.whereEqualTo("postedBy" , FirebaseAuth.getInstance().currentUser!!.uid).orderBy("createdAt", Query.Direction.DESCENDING)
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post::class.java).build()

        myAdapter = FireAdapter(recyclerViewOptions, requireContext())

        binding.userPostRv.adapter = myAdapter
        binding.userPostRv.layoutManager = LinearLayoutManager(requireContext())

        dialog.dismiss()
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


    override fun onStart() {
        super.onStart()
        myAdapter.startListening()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        myAdapter.stopListening()
    }


}