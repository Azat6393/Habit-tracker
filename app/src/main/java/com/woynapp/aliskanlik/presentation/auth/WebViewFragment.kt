package com.woynapp.aliskanlik.presentation.auth

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.woynapp.aliskanlik.R
import com.woynapp.aliskanlik.databinding.FragmentWebViewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WebViewFragment : Fragment(R.layout.fragment_web_view) {

    private lateinit var _binding: FragmentWebViewBinding
    private val args: WebViewFragmentArgs by navArgs()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentWebViewBinding.bind(view)

        _binding.webView.settings.apply {
            useWideViewPort = true
            javaScriptCanOpenWindowsAutomatically = true
            databaseEnabled = true
            domStorageEnabled = true
            javaScriptEnabled = true
            displayZoomControls = true
            cacheMode = WebSettings.LOAD_DEFAULT
            mediaPlaybackRequiresUserGesture = false

            allowContentAccess = true
            allowFileAccess = true

            setSupportMultipleWindows(false)
        }
        _binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                showProgressBar(false)
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                Toast.makeText(requireContext(), error?.description, Toast.LENGTH_SHORT).show()
            }
        }
        _binding.webView.loadUrl(args.url)
    }

    private fun showProgressBar(state: Boolean) {
        _binding.progressBar.isVisible = state
    }

    override fun onStart() {
        super.onStart()
        requireActivity()
            .findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
            .visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        requireActivity()
            .findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
            .visibility = View.VISIBLE
    }
}