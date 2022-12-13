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
import com.woynapp.aliskanlik.R
import com.woynapp.aliskanlik.core.utils.Resource
import com.woynapp.aliskanlik.core.utils.showToastMessage
import com.woynapp.aliskanlik.databinding.FragmentSignInBinding
import com.woynapp.aliskanlik.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignInFragment : Fragment(R.layout.fragment_sign_in) {

    private lateinit var _binding: FragmentSignInBinding
    private val viewModel: AuthViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSignInBinding.bind(view)

        _binding.apply {
            signUpBtn.setOnClickListener {
                val action = SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
                findNavController().navigate(action)
            }
            loginBtn.setOnClickListener {
                logIn()
            }
            forgotPasswordBtn.setOnClickListener {
                ForgotPasswordBottomSheet().show(childFragmentManager, "EmailLogInFragment")
            }
        }
        observe()
    }

    private fun logIn() {
        if (isAllInputsFilled()) {
            viewModel.signInWithEmail(
                email = _binding.emailEt.text.toString(),
                password = _binding.passwordEt.text.toString(),
            )
        }
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.signInResponse.collect {
                    when (it) {
                        is Resource.Empty -> isLoading(false)
                        is Resource.Error -> {
                            isLoading(false)
                            it.message?.let { it1 -> requireContext().showToastMessage(it1) }
                        }
                        is Resource.Loading -> isLoading(true)
                        is Resource.Success -> {
                            if (it.data?.phone_number?.isNotBlank() == true
                            ) {
                                val intent = Intent(requireActivity(), MainActivity::class.java)
                                startActivity(intent)
                                isLoading(false)
                                requireActivity().finish()
                            } else {
                                viewModel.clearSignInResponse()
                                it.data?.id?.let { id ->
                                    val action =
                                        SignInFragmentDirections.actionSignInFragmentToVerifyNumberFragment(
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
    }

    private fun isLoading(state: Boolean) {
        _binding.apply {
            progressBar.isVisible = state
            loginBtn.isVisible = !state
            signUpBtn.isVisible = !state
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
            else -> {
                true
            }
        }
    }
}
