package com.woynapp.aliskanlik.presentation.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.woynapp.aliskanlik.R
import com.woynapp.aliskanlik.databinding.FragmentAboutUsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutUsFragment : Fragment(R.layout.fragment_about_us) {

    private lateinit var _binding: FragmentAboutUsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAboutUsBinding.bind(view)

        _binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}