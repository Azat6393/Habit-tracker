package com.woynapp.aliskanlik.presentation.add_habit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.woynapp.aliskanlik.R
import com.woynapp.aliskanlik.databinding.BottomSheetChoseEmojiBinding
import com.woynapp.aliskanlik.domain.model.Emoji
import com.woynapp.aliskanlik.presentation.adapter.AdapterItemListener
import com.woynapp.aliskanlik.presentation.adapter.EmojiAdapter

class EmojiBottomSheet(private val list: List<Emoji>, private val onChoose: (String) -> Unit) :
    BottomSheetDialogFragment(), AdapterItemListener<Emoji> {

    private lateinit var _binding: BottomSheetChoseEmojiBinding
    private val mAdapter: EmojiAdapter by lazy { EmojiAdapter(this) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return layoutInflater.inflate(R.layout.bottom_sheet_chose_emoji, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = BottomSheetChoseEmojiBinding.bind(view)
        _binding.emojiRv.apply {
            adapter = mAdapter
            layoutManager = GridLayoutManager(requireContext(), 5)
            setHasFixedSize(true)
        }
        mAdapter.submitList(list)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    override fun onClick(item: Emoji) {
        onChoose(item.emoji)
        this.dismiss()
    }
}