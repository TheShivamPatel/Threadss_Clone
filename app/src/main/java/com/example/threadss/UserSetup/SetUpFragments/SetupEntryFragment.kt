package com.example.threadss.UserSetup.SetUpFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.threadss.R
import com.example.threadss.databinding.FragmentSetupEntryBinding

class SetupEntryFragment : Fragment() {

    private var _binding: FragmentSetupEntryBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSetupEntryBinding.inflate(inflater, container, false)

        binding.loginBtn.setOnClickListener {
            findNavController().navigate(R.id.action_setupEntryFragment_to_loginFragment)
        }

        binding.signUpBtn.setOnClickListener {
            findNavController().navigate(R.id.action_setupEntryFragment_to_registrationFragment)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}