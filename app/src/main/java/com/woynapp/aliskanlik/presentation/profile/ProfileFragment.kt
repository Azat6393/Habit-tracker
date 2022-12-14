package com.woynapp.aliskanlik.presentation.profile

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import com.woynapp.aliskanlik.R
import com.woynapp.aliskanlik.core.utils.*
import com.woynapp.aliskanlik.databinding.FragmentProfileBinding
import com.woynapp.aliskanlik.domain.model.User
import com.woynapp.aliskanlik.presentation.AuthActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var _binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels()

    private var isEditMode = false

    private var selectedImage: Uri? = null

    private var user: User? = null

    private val getContent: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetContent()) { imageUri: Uri? ->
            selectedImage = imageUri
            if (imageUri == null || imageUri.equals("")) {
                if (!user?.profile_photo.isNullOrBlank()) {
                    Picasso.get()
                        .load(user?.profile_photo)
                        .resize(800, 0)
                        .placeholder(R.drawable.avatar_icon)
                        .error(R.drawable.avatar_icon)
                        .into(_binding.profilePhotoIv)
                }
            } else {
                Picasso.get()
                    .load(selectedImage)
                    .resize(800, 0)
                    .placeholder(R.drawable.avatar_icon)
                    .error(R.drawable.avatar_icon)
                    .into(_binding.profilePhotoIv)
            }
        }

    private val requestGetContentPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getContent.launch("image/*")
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)
        observeUser()

        initView()
        initProfileDetails()
        observe()
    }

    private fun observeUser() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.currentUser.collect {
                    user = it
                }
            }
        }
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uploadingResponse.collect { result ->
                    when (result) {
                        is Resource.Empty -> _binding.progressBar.isVisible = false
                        is Resource.Error -> _binding.progressBar.isVisible = false
                        is Resource.Loading -> _binding.progressBar.isVisible = true
                        is Resource.Success -> {
                            _binding.progressBar.isVisible = false
                        }
                    }
                }
            }
        }
    }

    private fun saveChanges() {
        user?.let {
            val oldName = "${it.first_name} ${it.last_name}"
            if (oldName != _binding.userName.text.toString()) {
                // save new name
                if (_binding.userName.text.toString().isNotBlank()) {
                    viewModel.updateName(_binding.userName.text.toString())
                } else {
                    requireContext().showToastMessage(getString(R.string.input_first_name))
                }
            }
            if (selectedImage != null) {
                // save new profile photo
                selectedImage?.let {
                    viewModel.saveNewProfilePhoto(it)
                }
            }
        }
    }

    private fun changeEditMode(state: Boolean) {
        isEditMode = state
        _binding.apply {
            addBtn.isVisible = state
            editButton.isVisible = !state
            saveButton.isVisible = state
            if (state) {
                userName.inputType = InputType.TYPE_CLASS_TEXT
                userName.isEnabled = true
            } else {
                userName.inputType = InputType.TYPE_NULL
                userName.isEnabled = false
            }
        }
    }

    private fun initView() {
        _binding.apply {
            editButton.setOnClickListener {
                changeEditMode(true)
            }
            saveButton.setOnClickListener {
                saveChanges()
                changeEditMode(false)
            }
            profileImage.setOnClickListener {
                if (isEditMode) {
                    if (requireContext().checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        getContent.launch("image/*")
                    } else {
                        requestGetContentPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
            }
            signOutBtn.setOnClickListener {
                showAlertDialog(
                    requireContext(),
                    getString(R.string.sign_out),
                    getString(R.string.sign_out_message)
                ) {
                    viewModel.signOut()
                    val intent = Intent(requireActivity(), AuthActivity::class.java)
                    requireActivity().startActivity(intent)
                    requireActivity().finish()
                }
            }
            aboutBtn.setOnClickListener {
                val action = ProfileFragmentDirections.actionProfileFragmentToAboutUsFragment()
                findNavController().navigate(action)
            }
            websiteBtn.setOnClickListener { navigateToWebView("http://woynapp.com") }
            privacyPolictBtn.setOnClickListener { navigateToWebView("http://woynapp.com/gizlilik-politikasi/") }
            termOfService.setOnClickListener { navigateToWebView("http://woynapp.com/terms-of-service/") }

            val mSharedPreferences =
                requireActivity().getSharedPreferences(Constants.PREFERENCE_DATABASE_NAME, Context.MODE_PRIVATE)
            val editor = mSharedPreferences.edit()
            val isDarkMode = mSharedPreferences.getBoolean(Constants.PREFERENCE_DARK_MODE, false)
            darkModeSwitch.isChecked = isDarkMode
            darkModeSwitch.setOnCheckedChangeListener { compoundButton, b ->
                if (b) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    editor.putBoolean(Constants.PREFERENCE_DARK_MODE, true)
                    editor.apply()
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    editor.putBoolean(Constants.PREFERENCE_DARK_MODE, false)
                    editor.apply()
                }
            }
        }
    }

    private fun navigateToWebView(url: String) {
        val action = ProfileFragmentDirections.actionProfileFragmentToWebViewFragment(url)
        findNavController().navigate(action)
    }

    @SuppressLint("SetTextI18n")
    private fun initProfileDetails() {
        _binding.apply {
            user?.let {
                if (!it.profile_photo.isNullOrBlank()) {
                    Picasso.get()
                        .load(it.profile_photo)
                        .resize(800, 0)
                        .placeholder(R.drawable.avatar_icon)
                        .error(R.drawable.avatar_icon)
                        .into(_binding.profilePhotoIv)
                }
                userName.setText("${it.first_name} ${it.last_name}")
            }
        }
    }
}