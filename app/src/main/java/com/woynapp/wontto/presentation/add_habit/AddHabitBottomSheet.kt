package com.woynapp.wontto.presentation.add_habit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.woynapp.wontto.R
import com.woynapp.wontto.databinding.BottomSheetAddCategoryBinding

class AddHabitBottomSheet(private val addHabit: (String) -> Unit): BottomSheetDialogFragment() {

    private lateinit var _binding: BottomSheetAddCategoryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_add_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = BottomSheetAddCategoryBinding.bind(view)

        _binding.saveButton.setOnClickListener {
            val text = _binding.categoryNameInputLayout.editText?.text.toString()
            if (text.isNotBlank()){
                addHabit(text)
                dismiss()
            }
        }
    }
}