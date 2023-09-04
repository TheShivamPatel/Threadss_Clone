package com.example.threadss.UserSetup.mainfragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.threadss.R
import com.example.threadss.adapter.FireAdapter
import com.example.threadss.daos.PostDao
import com.example.threadss.adapter.UserPostAdapter
import com.example.threadss.databinding.FragmentHomeBinding
import com.example.threadss.models.Post
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var dialog: Dialog
    private lateinit var postDao: PostDao

    private lateinit var myAdapter: FireAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)


        // dialog box
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.progress_layout)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false)

        lifecycleScope.launch {
            setRecy()
        }


        return binding.root
    }
    private fun setRecy() {

        dialog.show()


        postDao = PostDao()
        val postsCollections = postDao.postCollections
        val query = postsCollections.orderBy("createdAt", Query.Direction.DESCENDING)
        val recyclerViewOptions =
            FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post::class.java).build()

        myAdapter = FireAdapter(recyclerViewOptions, requireContext())

        binding.rv.adapter = myAdapter
        binding.rv.layoutManager = LinearLayoutManager(requireContext())

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}