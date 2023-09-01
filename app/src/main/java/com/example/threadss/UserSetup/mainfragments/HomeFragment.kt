package com.example.threadss.UserSetup.mainfragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.threadss.R
import com.example.threadss.daos.PostDao
import com.example.threadss.adapter.UserPostAdapter
import com.example.threadss.databinding.FragmentHomeBinding
import com.example.threadss.models.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var dialog: Dialog
    private lateinit var postDao: PostDao



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // dialog box
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.progress_layout)
        dialog.getWindow()?.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false)

        lifecycleScope.launch {
            setUpRecyclerView()
        }


        return binding.root
    }

    private fun setUpRecyclerView() {


        dialog.show()


        postDao = PostDao()
        val postsCollections = postDao.postCollections

        val list = ArrayList<Post>()

        postsCollections.get().addOnSuccessListener {
            list.clear()
            for (doc in it.documents) {
                val data = doc.toObject(Post::class.java)
                list.add(data!!)
            }

            list.sortByDescending {
                it.createdAt
            }

            binding.rv.adapter = UserPostAdapter(requireContext(), list)
            binding.rv.layoutManager = LinearLayoutManager(context)
            dialog.dismiss()
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}