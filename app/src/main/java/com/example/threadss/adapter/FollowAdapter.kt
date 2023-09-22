package com.example.threadss.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.threadss.DetailProfileActivity
import com.example.threadss.databinding.FollowerLayoutItemBinding
import com.example.threadss.utils.USER_NODE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FollowAdapter(
    private val context: Context,
    private val list: ArrayList<*>,
    private var i: Int
) :
    RecyclerView.Adapter<FollowAdapter.followViewHolder>() {

    inner class followViewHolder(val binding: FollowerLayoutItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): followViewHolder {
        val view =
            FollowerLayoutItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return followViewHolder(view)
    }

    override fun onBindViewHolder(holder: followViewHolder, position: Int) {

        holder.binding.apply {

            val user = list[i]
            i++
            Firebase.firestore.collection(USER_NODE).document(user.toString()).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val doc = task.result
                        if (doc.exists()) {
                            val name = doc.getString("userName")
                            val profile = doc.getString("userProfile")

                            userName.text = name
                            Glide.with(context).load(profile).into(profileImage)

                        }
                    }
                }

            val currentUser = FirebaseAuth.getInstance().currentUser!!.uid

            Firebase.firestore.collection(USER_NODE).document(currentUser).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val doc = task.result
                        if (doc.exists()) {
                            val following = doc.get("following") as ArrayList<*>

                            if (following.contains(user)){
                                holder.binding.followText.text = "Following"
                            }
                        }

                    }

                }


            holder.binding.root.setOnClickListener {
                val intent = Intent(context, DetailProfileActivity::class.java)
                intent.putExtra("id", user.toString())
                context.startActivity(intent)
            }

        }

    }

    override fun getItemCount(): Int {
        return list.size
    }


}