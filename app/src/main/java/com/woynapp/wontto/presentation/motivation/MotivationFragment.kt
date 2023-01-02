package com.woynapp.wontto.presentation.motivation

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.woynapp.wontto.R
import com.woynapp.wontto.core.utils.*
import com.woynapp.wontto.databinding.FragmentMotivationBinding
import com.woynapp.wontto.domain.model.Motivation
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MotivationFragment : Fragment(R.layout.fragment_motivation) {

    private lateinit var _binding: FragmentMotivationBinding
    private var mSharedPreferences: SharedPreferences? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMotivationBinding.bind(view)

        mSharedPreferences =
            requireActivity().getSharedPreferences(
                Constants.PREFERENCE_DATABASE_NAME,
                Context.MODE_PRIVATE
            )

        val isDarkMode = mSharedPreferences?.getBoolean(Constants.PREFERENCE_DARK_MODE, false)
        isDarkMode?.let {
            _binding.backgroundImg.isVisible = !isDarkMode
        }

        initMotivation()
    }

    private fun initMotivation() {
        mSharedPreferences?.let { preferences ->
            val lastMotivationDate =
                preferences.getLong(Constants.PREFERENCE_LAST_MOTIVATION_DATE, 0)
            val currentDate = System.currentTimeMillis()
            if (lastMotivationDate.toDays() == currentDate.toDays()) {
                val motivationText =
                    preferences.getString(Constants.PREFERENCE_MOTIVATION_TEXT, "")
                val motivationWriter =
                    preferences.getString(Constants.PREFERENCE_MOTIVATION_WRITER, "")
                _binding.motivationText.text = motivationText
                _binding.writerTv.text = motivationWriter
            } else {
                setMotivationText()
            }
        }
    }

    private fun setMotivationText() {
        when (Locale.getDefault().language) {
            "ru" -> getMotivationFromAsset("motivation_ru.json")
            "tr" -> getMotivationFromAsset("motivation_tr.json")
            else -> getMotivationFromAsset("motivation_eng.json")
        }
    }

    private fun getMotivationFromAsset(name: String) {
        val motivationList =
            getJsonFromAssets(
                requireContext(),
                name
            )?.fromJsonToMotivation()
        val motivation = motivationList?.get(
            motivationList.indices.random()
        )
        motivation?.let { setAndSaveMotivation(it) }
    }

    private fun setAndSaveMotivation(motivation: Motivation) {
        _binding.motivationText.text = motivation.quote
        _binding.writerTv.text = motivation.author
        mSharedPreferences?.let {
            val editor = it.edit()
            editor.putString(Constants.PREFERENCE_MOTIVATION_TEXT, motivation.quote)
            editor.putString(Constants.PREFERENCE_MOTIVATION_WRITER, motivation.author)
            editor.putLong(Constants.PREFERENCE_LAST_MOTIVATION_DATE, System.currentTimeMillis())
            editor.apply()
        }
        PopUpDialog(
            onClose = {},
            onClick = {
                val action =
                    MotivationFragmentDirections.actionMotivationFragmentToWebViewFragment(
                        Constants.KARGO_BUL_URL
                    )
                findNavController().navigate(action)
            }
        ).show(childFragmentManager, "Ad Pop up")
    }
}