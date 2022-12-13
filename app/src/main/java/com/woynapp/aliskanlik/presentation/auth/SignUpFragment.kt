package com.woynapp.aliskanlik.presentation.auth

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
import com.woynapp.aliskanlik.R
import com.woynapp.aliskanlik.core.utils.Resource
import com.woynapp.aliskanlik.core.utils.showToastMessage
import com.woynapp.aliskanlik.databinding.FragmentSignUpBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment(R.layout.fragment_sign_up) {

    private lateinit var _binding: FragmentSignUpBinding
    private val viewModel: AuthViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSignUpBinding.bind(view)

        _binding.apply {
            signUp.setOnClickListener {
                createAccount()
            }
        }
        observe()
    }

    private fun createAccount() {
        if (isAllInputsFilled()) {
            viewModel.signUpWithEmail(
                email = _binding.emailEt.text.toString(),
                password = _binding.passwordEt.text.toString(),
                lastName = _binding.lastNameEt.text.toString(),
                firstName = _binding.nameEt.text.toString()
            )
        }
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.signUpResponse.collect {
                    when (it) {
                        is Resource.Empty -> isLoading(false)
                        is Resource.Error -> {
                            isLoading(false)
                            it.message?.let { it1 -> requireContext().showToastMessage(it1) }
                        }
                        is Resource.Loading -> isLoading(true)
                        is Resource.Success -> {
                            isLoading(false)
                            viewModel.clearSignUpResponse()
                            it.data?.id?.let { id ->
                                val action =
                                    SignUpFragmentDirections.actionSignUpFragmentToVerifyNumberFragment(
                                        id
                                    )
                                findNavController().navigate(action)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun isLoading(state: Boolean) {
        _binding.apply {
            progressBar.isVisible = state
            signUp.isVisible = !state
        }
    }

    private fun isAllInputsFilled(): Boolean {
        return when {
            _binding.emailEt.text.toString().isBlank() -> {
                requireContext().showToastMessage(getString(R.string.input_email_address))
                return false
            }
            _binding.passwordEt.text.toString().isBlank() -> {
                requireContext().showToastMessage(getString(R.string.input_password))
                return false
            }
            _binding.nameEt.text.toString().isBlank() -> {
                requireContext().showToastMessage(getString(R.string.input_first_name))
                return false
            }
            _binding.lastNameEt.text.toString().isBlank() -> {
                requireContext().showToastMessage(getString(R.string.input_last_name))
                return false
            }
            else -> {
                true
            }
        }
    }
}