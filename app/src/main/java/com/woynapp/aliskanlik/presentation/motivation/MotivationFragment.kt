package com.woynapp.aliskanlik.presentation.motivation

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.woynapp.aliskanlik.R
import com.woynapp.aliskanlik.core.utils.Constants
import com.woynapp.aliskanlik.core.utils.fromJsonToMotivation
import com.woynapp.aliskanlik.core.utils.getJsonFromAssets
import com.woynapp.aliskanlik.core.utils.toDays
import com.woynapp.aliskanlik.databinding.FragmentMotivationBinding
import com.woynapp.aliskanlik.domain.model.Motivation
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
                Constants.PREFERENCE_DARK_MODE,
                Context.MODE_PRIVATE
            )
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
    }
}