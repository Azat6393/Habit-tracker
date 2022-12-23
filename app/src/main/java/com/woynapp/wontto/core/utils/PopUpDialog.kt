package com.woynapp.wontto.core.utils

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.woynapp.wontto.R
import com.woynapp.wontto.databinding.FragmentPopUpBinding

class PopUpDialog(
    private val onClose: () -> Unit,
    private val onClick: () -> Unit
) : DialogFragment() {

    private lateinit var _binding: FragmentPopUpBinding

    private var clickedCloseBtn = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pop_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPopUpBinding.bind(view)

        val uri = Uri.parse("android.resource://${requireActivity().packageName}/${R.raw.video}")
        _binding.videoview.setVideoURI(uri)
        _binding.videoview.start()

        _binding.close.setOnClickListener {
            onClose()
            clickedCloseBtn = true
            this.dismiss()
        }
        _binding.root.setOnClickListener{
            _binding.videoview.stopPlayback()
            onClick()
            this.dismiss()
        }
    }

    override fun onResume() {
        _binding.videoview.resume()
        super.onResume()
    }

    override fun onPause() {
        _binding.videoview.suspend()
        super.onPause()
    }

    override fun onDestroy() {
        _binding.videoview.stopPlayback()
        super.onDestroy()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (clickedCloseBtn) onClose()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
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