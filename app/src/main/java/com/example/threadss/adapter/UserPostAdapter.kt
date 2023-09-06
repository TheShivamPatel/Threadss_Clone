package com.example.threadss.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.threadss.R
import com.example.threadss.databinding.PostItemLayoutBinding
import com.example.threadss.models.Post
import com.example.threadss.utils.DUMMY_IMAGE
import com.example.threadss.utils.GetTimeAgo
import com.example.threadss.utils.USER_NODE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserPostAdapter(
    private val context: Context,
    private val list: ArrayList<Post>
) :
    RecyclerView.Adapter<UserPostAdapter.posttViewHolder>() {
    inner class posttViewHolder(val postBinding: PostItemLayoutBinding) :
        RecyclerView.ViewHolder(postBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): posttViewHolder {
        val view = PostItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return posttViewHolder(view)
    }

    override fun onBindViewHolder(holder: posttViewHolder, position: Int) {
        var post = list[position]

        holder.postBinding.txtCaption.text = post.caption
        holder.postBinding.createdAt.text = GetTimeAgo.getTimeAgo(post.createdAt)
        holder.postBinding.likeTxt.text = "${post.likeBy.size} likes"

        if ( post.image != DUMMY_IMAGE){
            holder.postBinding.postImg.visibility = View.VISIBLE
            Glide.with(context).load(post.image).thumbnail(Glide.with(context).load(R.drawable.loading)).into(holder.postBinding.postImg)
        }


        val user = post.postedBy
        Firebase.firestore.collection(USER_NODE).document(user.toString()).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val doc = task.result
                    if (doc.exists()) {
                        val name = doc.getString("userName")
                        val profile = doc.getString("userProfile")
                        holder.postBinding.userName.text = name
                        Glide.with(context).load(profile).into(holder.postBinding.userImage)

                    }
                }
            }


        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        val isLiked = post.likeBy.contains(currentUser)

        if (isLiked == true) {
            holder.postBinding.likeBtn.setImageResource(R.drawable.red_heart)
        }
//
//        // database instance
//        val db = Firebase.firestore
//        val currentPost = db.collection(POST_NODE).document(post.postDocID!!)
//
////        // like button on click
//        holder.postBinding.likeBtn.setOnClickListener {
//
//                if (list[position].likeBy.contains(currentUser)) {
//                    currentPost.update("likeBy", FieldValue.arrayRemove(currentUser))
//                    holder.postBinding.likeBtn.setImageResource(R.drawable.like_off)
//                } else {
//                    currentPost.update("likeBy", FieldValue.arrayUnion(currentUser))
//                    holder.postBinding.likeBtn.setImageResource(R.drawable.red_heart)
//                }
//
//        }


        holder.postBinding.shareBtn.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT,
                    "${post.caption} - Download Threadss app for more such content."
                )
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            context.startActivity(shareIntent)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}