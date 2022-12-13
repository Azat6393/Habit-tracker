package com.woynapp.aliskanlik.presentation.profile

import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.woynapp.aliskanlik.core.utils.Constants
import com.woynapp.aliskanlik.core.utils.Constants.FIREBASE_STORAGE_PROFILE_IMAGES_CHILD
import com.woynapp.aliskanlik.core.utils.Resource
import com.woynapp.aliskanlik.data.local.datastore.DatastorePreferencesKey
import com.woynapp.aliskanlik.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val _uploadingResponse = MutableStateFlow<Resource<String>>(Resource.Empty())
    val uploadingResponse = _uploadingResponse.asStateFlow()

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

    fun saveNewProfilePhoto(uri: Uri) = viewModelScope.launch {
        val user = currentUser.first()
        _uploadingResponse.value = Resource.Loading<String>()
        val storageRef = Firebase.storage.getReference(FIREBASE_STORAGE_PROFILE_IMAGES_CHILD)
        val profileImagesRef = storageRef.child(user.id!!)
        val imageRef =
            profileImagesRef.child("${UUID.randomUUID()}.jpg")
        imageRef.putFile(uri)
            .addOnSuccessListener {
                it.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { url ->
                        val db = Firebase.firestore
                        db.collection(Constants.FIREBASE_FIRESTORE_USERS_COLLECTION)
                            .document(user.id)
                            .update("profile_photo", url.toString())
                            .addOnSuccessListener {
                                viewModelScope.launch {
                                    dataStore.edit { preferences ->
                                        preferences[DatastorePreferencesKey.USER_PROFILE_PHOTO_KEY] =
                                            url.toString()
                                        user.profile_photo = url.toString()
                                        _uploadingResponse.value =
                                            Resource.Success<String>(url.toString())
                                    }
                                }
                            }
                    }

            }.addOnFailureListener {
                _uploadingResponse.value = Resource.Error<String>(it.localizedMessage)
                println("Error: ${it.localizedMessage}")
            }
    }

    fun updateName(name: String) = viewModelScope.launch {
        val user = currentUser.first()
        val firstName = name.split(" ")[0]
        val lastName = name.split(" ")[1]
        val db = Firebase.firestore
        db.collection(Constants.FIREBASE_FIRESTORE_USERS_COLLECTION)
            .document(user.id!!)
            .update("first_name", firstName)
            .addOnSuccessListener {
                viewModelScope.launch {
                    dataStore.edit { preferences ->
                        preferences[DatastorePreferencesKey.USER_FIRST_NAME_KEY] = firstName
                        user.first_name = firstName
                    }
                }
            }
        db.collection(Constants.FIREBASE_FIRESTORE_USERS_COLLECTION)
            .document(user.id)
            .update("last_name", lastName)
            .addOnSuccessListener {
                viewModelScope.launch {
                    dataStore.edit { preferences ->
                        preferences[DatastorePreferencesKey.USER_LAST_NAME_KEY] = lastName
                        user.last_name = lastName
                    }
                }
            }
    }

    fun signOut() = viewModelScope.launch {
        Firebase.auth.signOut()
        dataStore.edit { preferences ->
            preferences[DatastorePreferencesKey.USER_ID_KEY] = ""
            preferences[DatastorePreferencesKey.USER_FIRST_NAME_KEY] = ""
            preferences[DatastorePreferencesKey.USER_LAST_NAME_KEY] = ""
            preferences[DatastorePreferencesKey.USER_PHONE_NUMBER_KEY] = ""
            preferences[DatastorePreferencesKey.USER_PROFILE_PHOTO_KEY] = ""
            preferences[DatastorePreferencesKey.USER_EMAIL_KEY] = ""
            preferences[DatastorePreferencesKey.USER_CREATED_DATE_KEY] = 0
        }
    }
}