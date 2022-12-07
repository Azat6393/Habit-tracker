package com.woynapp.aliskanlik.presentation.add_habit

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.aghajari.emojiview.view.AXSingleEmojiView
import com.woynapp.aliskanlik.R
import com.woynapp.aliskanlik.databinding.FragmentAddHabitBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddHabitFragment : Fragment(R.layout.fragment_add_habit) {

    private lateinit var _binding: FragmentAddHabitBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddHabitBinding.bind(view)


        _binding.daysSeekBar.setOnSeekBarChangeListener(onSeekbarChangeListener)

    }

    private val onSeekbarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            _binding.daySize.text = progress.toString()
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
        }
    }
}