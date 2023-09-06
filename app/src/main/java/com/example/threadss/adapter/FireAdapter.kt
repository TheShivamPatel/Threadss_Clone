package com.example.threadss.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.threadss.DetailProfileActivity
import com.example.threadss.R
import com.example.threadss.databinding.PostItemLayoutBinding
import com.example.threadss.models.Post
import com.example.threadss.utils.DUMMY_IMAGE
import com.example.threadss.utils.GetTimeAgo
import com.example.threadss.utils.POST_NODE
import com.example.threadss.utils.USER_NODE
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FireAdapter(options: FirestoreRecyclerOptions<Post>, val context: Context) :
    FirestoreRecyclerAdapter<Post, FireAdapter.postHolder>(
        options
    ) {
    class postHolder(val binding: PostItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): postHolder {
        val view = PostItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return postHolder(view)
    }

    override fun onBindViewHolder(holder: postHolder, position: Int, model: Post) {
        holder.binding.apply {

            txtCaption.text = model.caption
            createdAt.text = GetTimeAgo.getTimeAgo(model.createdAt)
            likeTxt.text = "${model.likeBy.size} likes"


            if (model.image != DUMMY_IMAGE) {
                postImg.visibility = View.VISIBLE
                Glide.with(context).load(model.image)
                    .thumbnail(Glide.with(context).load(R.drawable.loading)).into(postImg)
            }


            val user = model.postedBy
            Firebase.firestore.collection(USER_NODE).document(user.toString()).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val doc = task.result
                        if (doc.exists()) {
                            val name = doc.getString("userName")
                            val profile = doc.getString("userProfile")
                            userName.text = name
                            Glide.with(context).load(profile).into(userImage)
                        }
                    }
                }

            val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
            val isLiked = model.likeBy.contains(currentUser)

            if (isLiked) {
                likeBtn.setImageResource(R.drawable.red_heart)
            } else {
                likeBtn.setImageResource(R.drawable.like_off)
            }

            // database instance
            val db = Firebase.firestore
            val currentPost = db.collection(POST_NODE).document(model.postDocID!!)

//        // like button on click
            likeBtn.setOnClickListener {

                if (isLiked) {
                    currentPost.update("likeBy", FieldValue.arrayRemove(currentUser))
                } else {
                    currentPost.update("likeBy", FieldValue.arrayUnion(currentUser))
                }

            }


            // SHARE POST CONTENT
            shareBtn.setOnClickListener {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "${model.caption} - Download Threadss app for more such content."
                    )
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                context.startActivity(shareIntent)
            }

            userImage.setOnClickListener {
                val intent = Intent(context, DetailProfileActivity::class.java)
                intent.putExtra("id", model.postedBy)
                context.startActivity(intent)
            }

        }
    }

}