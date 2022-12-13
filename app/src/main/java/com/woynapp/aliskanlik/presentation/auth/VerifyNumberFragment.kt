package com.woynapp.aliskanlik.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.decode.SvgDecoder
import coil.load
import coil.transform.CircleCropTransformation
import com.woynapp.aliskanlik.R
import com.woynapp.aliskanlik.core.utils.*
import com.woynapp.aliskanlik.databinding.FragmentVerifyNumberBinding
import com.woynapp.aliskanlik.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VerifyNumberFragment : Fragment(R.layout.fragment_verify_number) {

    private lateinit var _binding: FragmentVerifyNumberBinding

    private val viewModel: AuthViewModel by activityViewModels()
    private var selectedCountry: CountryInfo? = null
    private val args: VerifyNumberFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentVerifyNumberBinding.bind(view)

        _binding.closeButton.setOnClickListener {
            viewModel.signOut()
            findNavController().popBackStack()
        }
        _binding.continueBtn.setOnClickListener {
            if (isInputFilled()) {
                viewModel.updatePhoneNumber(
                    "${selectedCountry?.number}${_binding.phoneNumberTv.text}",
                    args.userId
                )
            }
        }
        initAutoComplete()
        observe()
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.phoneNumberResponse.collect { result ->
                    when (result) {
                        is Resource.Empty -> isLoading(false)
                        is Resource.Error -> {
                            isLoading(false)
                            result.message?.let { requireContext().showToastMessage(it) }
                        }
                        is Resource.Loading -> isLoading(true)
                        is Resource.Success -> {
                            val intent = Intent(requireActivity(), MainActivity::class.java)
                            startActivity(intent)
                            isLoading(false)
                            requireActivity().finish()
                        }
                    }
                }
            }
        }
    }

    private fun isLoading(state: Boolean) {
        _binding.apply {
            progressBar.isVisible = state
            continueBtn.isVisible = !state
        }
    }

    private fun initAutoComplete() {
        val countryList = getJsonFromAssets(requireContext(), Constants.countryListJsonName)
            ?.fromJsonToCountyList()
        val currentCountry = countryList?.find { it.name == "Turkey" }
        selectedCountry = currentCountry
        currentCountry?.let { fillCountryInfo(it) }
        _binding.apply {
            countryInfoContainer.setOnClickListener {
                if (countryList != null) {
                    CountriesDialog(countryList) {
                        fillCountryInfo(it)
                    }.show(childFragmentManager, "Country Dialog")
                }
            }
        }
    }

    private fun fillCountryInfo(countryInfo: CountryInfo) {
        _binding.apply {
            countryFlag.load(countryInfo.flag) {
                crossfade(true)
                placeholder(R.drawable.add_image_icon)
                decoderFactory(SvgDecoder.Factory())
                transformations(CircleCropTransformation())
                build()
            }
            countryCodeNumber.text = countryInfo.number
            selectedCountry = countryInfo
        }
    }

    private fun isInputFilled(): Boolean {
        return when {
            _binding.phoneNumberTv.text.toString().isBlank() -> {
                requireContext().showToastMessage(getString(R.string.input_phone_number))
                false
            }
            else -> true
        }
    }
}