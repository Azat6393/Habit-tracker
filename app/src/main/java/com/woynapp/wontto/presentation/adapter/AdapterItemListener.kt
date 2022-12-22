package com.woynapp.wontto.presentation.adapter

import com.woynapp.wontto.domain.model.HabitWithDays

interface AdapterItemListener<T> {
    fun onClick(item: T)
}

interface StartedHabitsItemListener{
    fun onClick(item: HabitWithDays)
    fun onClickAd()
}