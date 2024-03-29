package com.woynapp.wontto.presentation.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.woynapp.wontto.core.utils.Constants.FIREBASE_FIRESTORE_USERS_COLLECTION
import com.woynapp.wontto.core.utils.Resource
import com.woynapp.wontto.data.local.datastore.DatastorePreferencesKey
import com.woynapp.wontto.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    val currentUser = dataStore.data.map { preferences ->
        User(
            id = preferences[DatastorePreferencesKey.USER_ID_KEY] ?: "",
            first_name = preferences[DatastorePreferencesKey.USER_FIRST_NAME_KEY] ?: "",
            last_name = preferences[DatastorePreferencesKey.USER_LAST_NAME_KEY] ?: "",
            phone_number = preferences[DatastorePreferencesKey.USER_PHONE_NUMBER_KEY] ?: "",
            profile_photo = preferences[DatastorePreferencesKey.USER_PROFILE_PHOTO_KEY] ?: "",
            email = preferences[DatastorePreferencesKey.USER_EMAIL_KEY] ?: "",
            created_date = preferences[DatastorePreferencesKey.USER_CREATED_DATE_KEY] ?: 0
        )
    }

    private val _isAuth = MutableStateFlow(false)
    val isAuth = _isAuth.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _signUpResponse = MutableStateFlow<Resource<User>>(Resource.Empty())
    val signUpResponse = _signUpResponse.asStateFlow()

    private val _signInResponse = MutableStateFlow<Resource<User>>(Resource.Empty())
    val signInResponse = _signInResponse.asStateFlow()

    private val _phoneNumberResponse = MutableStateFlow<Resource<String>>(Resource.Empty())
    val phoneNumberResponse = _phoneNumberResponse.asStateFlow()

    init {
        _isAuth.value = Firebase.auth.currentUser != null
        viewModelScope.launch {
            delay(1000)
            _isLoading.value = false
        }
    }

    private fun updateCurrentUser(user: User) = viewModelScope.launch {
        dataStore.edit { preferences ->
            preferences[DatastorePreferencesKey.USER_ID_KEY] = user.id ?: ""
            preferences[DatastorePreferencesKey.USER_FIRST_NAME_KEY] = user.first_name ?: ""
            preferences[DatastorePreferencesKey.USER_LAST_NAME_KEY] = user.last_name ?: ""
            preferences[DatastorePreferencesKey.USER_PHONE_NUMBER_KEY] = user.phone_number ?: ""
            preferences[DatastorePreferencesKey.USER_PROFILE_PHOTO_KEY] = user.profile_photo ?: ""
            preferences[DatastorePreferencesKey.USER_EMAIL_KEY] = user.email ?: ""
            preferences[DatastorePreferencesKey.USER_CREATED_DATE_KEY] = user.created_date ?: 0
            preferences[DatastorePreferencesKey.CONTACTS_UPLOADED_KEY] = user.contacts_uploaded ?: false
        }
    }


    private fun updatePhoneNumberFromDataStore(number: String) = viewModelScope.launch {
        dataStore.edit { preferences ->
            preferences[DatastorePreferencesKey.USER_PHONE_NUMBER_KEY] = number
        }
    }

    fun signUpWithEmail(
        email: String,
        password: String,
        lastName: String,
        firstName: String,
    ) = viewModelScope.launch {
        _signUpResponse.value = Resource.Loading<User>()
        val auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                it.user?.let { user ->
                    createUser(
                        User(
                            id = user.uid,
                            first_name = firstName,
                            last_name = lastName,
                            phone_number = "",
                            profile_photo = "",
                            email = email,
                            created_date = System.currentTimeMillis()
                        )
                    )
                }
            }
            .addOnFailureListener {
                _signUpResponse.value = Resource.Error<User>(it.localizedMessage ?: "Error")
            }
    }

    fun signInWithEmail(
        email: String, password: String,
    ) = viewModelScope.launch {
        _signInResponse.value = Resource.Loading<User>()
        val auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                it.user?.let { user ->
                    getUser(user.uid)
                }
            }
            .addOnFailureListener {
                _signInResponse.value = Resource.Error<User>(it.localizedMessage ?: "Error")
            }
    }

    fun logInWithGoogle(idToken: String, firstName: String, lastName: String) =
        viewModelScope.launch {
            val auth = Firebase.auth
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(firebaseCredential)
                .addOnSuccessListener {
                    it.user?.let { user ->
                        getUserIfExists(
                            User(
                                id = user.uid,
                                first_name = firstName,
                                last_name = lastName,
                                phone_number = user.phoneNumber ?: "",
                                profile_photo = user.photoUrl.toString() ?: "",
                                email = user.email,
                                created_date = System.currentTimeMillis()
                            )
                        )
                    }
                }
                .addOnFailureListener {
                    _signUpResponse.value =
                        Resource.Error<User>(it.localizedMessage ?: "Error")
                }
        }

    private fun getUserIfExists(user: User) {
        _signInResponse.value = Resource.Loading<User>()
        val db = Firebase.firestore
        db.collection(FIREBASE_FIRESTORE_USERS_COLLECTION)
            .document(user.id!!)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val document = it.result
                    if (document.exists()) {
                        val response = document.toObject(User::class.java)
                        response?.let { result ->
                            updateCurrentUser(result)
                            _signUpResponse.value = Resource.Success<User>(result)
                        }
                    } else {
                        createUser(user)
                    }
                } else {
                    _signUpResponse.value =
                        Resource.Error<User>(it.exception?.localizedMessage ?: "Error")
                }
            }
    }

    private fun getUser(id: String) {
        _signInResponse.value = Resource.Loading<User>()
        val db = Firebase.firestore
        db.collection(FIREBASE_FIRESTORE_USERS_COLLECTION)
            .document(id)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(User::class.java)
                user?.let {
                    updateCurrentUser(it)
                    _signInResponse.value = Resource.Success<User>(it)
                }
            }
            .addOnFailureListener {
                _signInResponse.value = Resource.Error<User>(it.localizedMessage ?: "Error")
            }
    }

    private fun createUser(
        user: User
    ) {
        _signUpResponse.value = Resource.Loading<User>()
        val db = Firebase.firestore
        db.collection(FIREBASE_FIRESTORE_USERS_COLLECTION)
            .document(user.id!!)
            .set(user)
            .addOnSuccessListener {
                updateCurrentUser(user)
                _signUpResponse.value = Resource.Success<User>(user)
            }
            .addOnFailureListener { e ->
                _signUpResponse.value = Resource.Error<User>(e.localizedMessage ?: "Error")
            }
    }

    fun updatePhoneNumber(
        number: String,
        userId: String
    ) {
        _phoneNumberResponse.value = Resource.Loading<String>()
        val db = Firebase.firestore
        db.collection(FIREBASE_FIRESTORE_USERS_COLLECTION)
            .document(userId)
            .update("phone_number", number)
            .addOnSuccessListener {
                updatePhoneNumberFromDataStore(number)
                _phoneNumberResponse.value = Resource.Success<String>("Successful")
            }
            .addOnFailureListener { e ->
                _phoneNumberResponse.value = Resource.Error<String>(e.localizedMessage ?: "Error")
            }
    }


    fun signOut() = viewModelScope.launch {
        _isAuth.value = false
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

    fun clearSignUpResponse() {
        _signUpResponse.value = Resource.Empty<User>()
    }

    fun clearSignInResponse(){
        _signInResponse.value = Resource.Empty<User>()
    }
}