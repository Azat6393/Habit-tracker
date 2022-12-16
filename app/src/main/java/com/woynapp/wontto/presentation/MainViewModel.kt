package com.woynapp.wontto.presentation

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woynapp.wontto.core.utils.Constants
import com.woynapp.wontto.core.utils.DefaultCategoryAndHabit
import com.woynapp.wontto.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repo: HabitRepository,
    private val application: Application
) : ViewModel() {

    private val mSharedPreferences: SharedPreferences by lazy {
        application.getSharedPreferences(Constants.PREFERENCE_DATABASE_NAME, Context.MODE_PRIVATE)
    }
    private val editor: SharedPreferences.Editor by lazy {
        mSharedPreferences.edit()
    }

    fun checkIsFirstTime() = viewModelScope.launch {
        val isFirstTime = mSharedPreferences.getBoolean(Constants.PREFERENCE_FIRST_TIME, true)
        if (isFirstTime) {
            DefaultCategoryAndHabit.getCategories(context = application).forEach { category ->
                repo.insertCategory(category)
            }.also {
                DefaultCategoryAndHabit.getHabits(context = application).forEach { habit ->
                    repo.insertHabit(habit)
                }
            }
            editor.putBoolean(Constants.PREFERENCE_FIRST_TIME, false)
            editor.apply()
        }
    }
}