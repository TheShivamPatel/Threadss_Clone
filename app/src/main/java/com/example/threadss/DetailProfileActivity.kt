package com.example.threadss

import android.app.Dialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.threadss.adapter.FireAdapter
import com.example.threadss.daos.PostDao
import com.example.threadss.databinding.ActivityDetailProfileBinding
import com.example.threadss.models.Post
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query

class DetailProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailProfileBinding

    private var name: String? = null
    private var profile: String? = null
    private lateinit var dialog: Dialog
    private lateinit var postDao: PostDao
    private lateinit var myCurrentUserId: String
    private lateinit var myAdapter: FireAdapter

    private var isIamFollowing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        postDao = PostDao()
        myCurrentUserId = FirebaseAuth.getInstance().currentUser?.uid!!

        // progress dialog box
        dialog = Dialog(this)
        dialog.setContentView(R.layout.progress_layout)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false)


        val postedBy = intent.getStringExtra("id")

        if(myCurrentUserId != postedBy){
            binding.followBtn.visibility = View.VISIBLE
        }


        getUserDetails(postedBy)


        setRecy(postedBy!!)

        binding.followBtn.setOnClickListener {

            followFunctionCalled(postedBy)
        }


    }


    private fun setRecy(postedBy: String) {

        dialog.show()


        postDao = PostDao()
        val postsCollections = postDao.postCollections
        val query = postsCollections.orderBy("createdAt", Query.Direction.DESCENDING).whereEqualTo("postedBy" , postedBy)
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post::class.java).build()

        myAdapter = FireAdapter(recyclerViewOptions, this)

        binding.userPostRv.adapter = myAdapter
        binding.userPostRv.layoutManager = LinearLayoutManager(this)

        dialog.dismiss()
    }


    override fun onStart() {
        super.onStart()
        myAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        myAdapter.stopListening()
    }



    private fun getUserDetails(postedBy: String?) {


        postDao.userCollection.document(postedBy!!).get()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    val doc = task.result
                    if (doc.exists()) {
                        name = doc.getString("userName")
                        profile = doc.getString("userProfile")
                        val desc = doc.getString("profileDescription")
                        val followers = doc.get("followers") as List<String>

                        if (followers.contains(myCurrentUserId)) {
                            isIamFollowing = true
                            binding.followBtn.apply {
                                text = "Following"
                                setBackgroundColor(Color.WHITE)

                                setTextColor(ContextCompat.getColor(this@DetailProfileActivity, R.color.black))

                            }
                        }else{
                            isIamFollowing = false
                            binding.followBtn.apply {
                                text = "Follow"
                                setBackgroundColor(Color.BLACK)
                                setTextColor(ContextCompat.getColor(this@DetailProfileActivity, R.color.white))
                            }
                        }

                        binding.textView8.text = "${followers.size} followers"
                        binding.userNameTxt.text = name.toString()
                        binding.textView6.text = desc
                        Glide.with(this).load(profile).into(binding.userImage)
                    }
                }

            }


    }


    private fun followFunctionCalled(postedBy: String) {


        val friendUserId = this.postDao.userCollection.document(postedBy)
        val me = this.postDao.userCollection.document(myCurrentUserId)

        if (isIamFollowing) {
            friendUserId.update("followers", FieldValue.arrayRemove(myCurrentUserId))
            me.update("following" , FieldValue.arrayRemove(postedBy))

        } else {
            friendUserId.update("followers", FieldValue.arrayUnion(myCurrentUserId))
            me.update("following" , FieldValue.arrayUnion(postedBy))
        }

        getUserDetails(postedBy)

    }


}