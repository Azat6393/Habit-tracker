package com.woynapp.aliskanlik.presentation.auth

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.woynapp.aliskanlik.R
import com.woynapp.aliskanlik.core.utils.Resource
import com.woynapp.aliskanlik.core.utils.showToastMessage
import com.woynapp.aliskanlik.databinding.FragmentAuthBinding
import com.woynapp.aliskanlik.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthFragment : Fragment(R.layout.fragment_auth) {

    private lateinit var _binding: FragmentAuthBinding

    private val TAG: String = "Google Sign in"
    private lateinit var oneTapClient: SignInClient
    private lateinit var singInRequest: BeginSignInRequest
    private val viewModel: AuthViewModel by viewModels()
    private val REQ_ONE_TAP = 2
    private var showOneTapUI = true


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAuthBinding.bind(view)

        observeDatastore()
        initView()
        observe()
    }

    private fun initView() {
        _binding.apply {
            signUpBtn.setOnClickListener {
                // TODO navigate to SingUpFragment
            }
            signInBtn.setOnClickListener {
                // TODO navigate to SignInFragment
            }
            loginWithGoogleBtn.setOnClickListener {
                initGoogleSignInRequest()
                beginSignIn()
            }
        }
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun observeDatastore() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentUser.collect { user ->
                    val isAuth = viewModel.isAuth.value
                    if (isAuth && user.phone_number.toString().isBlank()) {
                        //TODO navigate to VerifyNumberFragment
                    }
                }
            }
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
                            if (it.data?.phone_number?.isNotBlank() == true
                            ) {
                                val intent = Intent(requireActivity(), MainActivity::class.java)
                                startActivity(intent)
                                isLoading(false)
                                requireActivity().finish()

                            } else {
                                it.data?.id?.let { id ->
                                    // TODO navigate to VerifyNumberFragment
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
            loginWithGoogleBtn.isVisible = !state
        }
    }

    private fun beginSignIn() {
        oneTapClient.beginSignIn(singInRequest)
            .addOnSuccessListener { result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, REQ_ONE_TAP,
                        null, 0, 0, 0, null
                    )
                } catch (e: IntentSender.SendIntentException) {
                    Log.e(
                        "Google Sign in",
                        "Couldn't start One Tap UI: ${e.localizedMessage}"
                    )
                    requireContext().showToastMessage("Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }.addOnFailureListener { e ->
                Log.d("Google Sign in", e.localizedMessage ?: "Error")
                requireContext().showToastMessage("Google Sign in Error is ${e.localizedMessage}")
            }
    }

    private fun initGoogleSignInRequest() {
        oneTapClient = Identity.getSignInClient(requireActivity())
        singInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(
                BeginSignInRequest.PasswordRequestOptions.builder()
                    .setSupported(true)
                    .build()
            )
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId("318115625526-9hfk3hi0gnv84vabammnpc0doausqovk.apps.googleusercontent.com")
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    val firstName = credential.givenName
                    val lastName = credential.familyName
                    val password = credential.password

                    when {
                        idToken != null -> {
                            // Got an ID token from Google. Use it to authenticate
                            // with your backend.
                            viewModel.logInWithGoogle(idToken, firstName ?: "", lastName ?: "")
                            Log.d("Google Sign in", "Got ID token.")
                        }
                        password != null -> {
                            // Got a saved username and password. Use them to authenticate
                            // with your backend.
                            Log.d("Google Sign in", "Got password.")
                        }
                        else -> {
                            Log.d("Google Sign in", "No ID token or password!")
                        }
                    }
                } catch (e: ApiException) {
                    when (e.statusCode) {
                        CommonStatusCodes.CANCELED -> {
                            Log.d(TAG, "One-tap dialog was closed.")
                            // Don't re-prompt the user.
                            showOneTapUI = false
                        }
                        CommonStatusCodes.NETWORK_ERROR -> {
                            Log.d(TAG, "One-tap encountered a network error.")
                            // Try again or just ignore.
                        }
                        else -> {
                            Log.d(
                                TAG, "Couldn't get credential from result." +
                                        " (${e.localizedMessage})"
                            )
                        }
                    }
                    requireContext().showToastMessage("Error is: ${e.localizedMessage}")
                }
            }
        }
    }
}