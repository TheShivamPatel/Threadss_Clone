package com.example.threadss

import android.app.Dialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.threadss.adapter.UserPostAdapter
import com.example.threadss.daos.PostDao
import com.example.threadss.databinding.ActivityDetailProfileBinding
import com.example.threadss.models.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue

class DetailProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailProfileBinding

    private var name: String? = null
    private var profile: String? = null
    private lateinit var dialog: Dialog
    private lateinit var postDao: PostDao
    private lateinit var myCurrentUserId: String

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

        getCategoryFromFirebase(postedBy!!)

        binding.followBtn.setOnClickListener {

            followFunctionCalled(postedBy)
        }


    }

    private fun getUserDetails(postedBy: String?) {



        postDao.userCollection.document(postedBy!!).get()
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    val doc = task.result
                    if (doc.exists()) {
                        name = doc.getString("userName")
                        profile = doc.getString("userProfile")
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


    private fun getCategoryFromFirebase(postedBy: String) {

        dialog.show()

        val postsCollections = this.postDao.postCollections

        val list = ArrayList<Post>()

        postsCollections.get().addOnSuccessListener {
            list.clear()
            for (doc in it.documents) {

                val data = doc.toObject(Post::class.java)

                if (data!!.postedBy.equals(postedBy)) {
                    list.add(data!!)
                }
            }
            list.sortByDescending {
                it.createdAt
            }
            binding.userPostRv.adapter = UserPostAdapter(this, list)
            binding.userPostRv.layoutManager = LinearLayoutManager(this)

            dialog.dismiss()
        }

    }


}