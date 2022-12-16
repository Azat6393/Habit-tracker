package com.woynapp.wontto.presentation.dashboard

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woynapp.wontto.data.local.datastore.DatastorePreferencesKey
import com.woynapp.wontto.domain.model.DayInfo
import com.woynapp.wontto.domain.model.HabitWithDays
import com.woynapp.wontto.domain.model.User
import com.woynapp.wontto.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repo: HabitRepository,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val _habits = MutableStateFlow<List<HabitWithDays>>(emptyList())
    val habits = _habits.asStateFlow()

    val currentUser = dataStore.data.map { preferences ->
        User(
            id = preferences[DatastorePreferencesKey.USER_ID_KEY],
            first_name = preferences[DatastorePreferencesKey.USER_FIRST_NAME_KEY],
            last_name = preferences[DatastorePreferencesKey.USER_LAST_NAME_KEY],
            phone_number = preferences[DatastorePreferencesKey.USER_PHONE_NUMBER_KEY],
            profile_photo = preferences[DatastorePreferencesKey.USER_PROFILE_PHOTO_KEY],
            email = preferences[DatastorePreferencesKey.USER_EMAIL_KEY],
            created_date = preferences[DatastorePreferencesKey.USER_CREATED_DATE_KEY]
        )
    }

    init {
        getStartedHabits()
    }

    private fun getStartedHabits() = viewModelScope.launch {
        repo.getAllHabitWithDays().onEach {
            _habits.value = it
        }.launchIn(viewModelScope)
    }


    fun updateDay(dayInfo: DayInfo) = viewModelScope.launch {
        repo.updateDayInfo(dayInfo)
    }
}