package com.example.threadss.UserSetup.mainfragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.threadss.R
import com.example.threadss.adapter.UserPostAdapter
import com.example.threadss.databinding.FragmentHomeBinding
import com.example.threadss.models.Post
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    private lateinit var dialog: Dialog


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

        setUpRecyclerView()


        return binding.root
    }


    private fun setUpRecyclerView() {

        dialog.show()

        val tempList = ArrayList<Post>()
        val list = ArrayList<Post>()

        Firebase.firestore.collection("posts")
            .get().addOnSuccessListener {
                list.clear()
                tempList.clear()
                for (doc in it.documents) {
                    val data = doc.toObject(Post::class.java)
                    list.add(data!!)
                }
                tempList.addAll(list)

                tempList.sortByDescending {
                    it.createdAt
                }

                binding.rv.adapter = UserPostAdapter(requireContext() ,tempList)
                binding.rv.layoutManager = LinearLayoutManager(context)

                dialog.dismiss()
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}