package com.example.c323_project_7_note

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import androidx.lifecycle.asLiveData
import com.example.c323_project_7_note.model.Note

/**
 * ViewModel shared across multiple fragments, handling business logic and data storage
 * @param application The application this ViewModel is associated with
 */
class SharedViewModel(application: Application) : AndroidViewModel(application) {

    private val accountService: AccountService = FirebaseAccountService()
    private val storageService: StorageService = FirebaseStorageService()

    private val _notes = MutableLiveData<List<Note>>()
    val notes: LiveData<List<Note>>
        get() = _notes

    private val _selectedNote = MutableLiveData<Note?>()
    val selectedNote: LiveData<Note?>
        get() = _selectedNote

    private val _authenticationState = MutableLiveData<AuthenticationState>()
    val authenticationState: LiveData<AuthenticationState>
        get() = _authenticationState

    /**
     * Setting up authentication state for the user
     */
    init {
        viewModelScope.launch {
            accountService.currentUser.collect { user ->
                if (user != null) {
                    _authenticationState.value = AuthenticationState.AUTHENTICATED
                    _notes.value = emptyList()
                    user.id?.let { userId ->
                        viewModelScope.launch {
                            storageService.notes(userId).collect { notes ->
                                _notes.value = notes
                            }
                        }
                    }
                } else {
                    _notes.value = emptyList()
                    _authenticationState.value = AuthenticationState.UNAUTHENTICATED
                }
            }
        }
    }

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?>
        get() = _errorMessage

    /**
     * Attempts to sign in the user
     * @param email The user's email
     * @param password The user's password
     */
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                accountService.signIn(email, password)
            } catch (e: FirebaseAuthException) {
                _authenticationState.value = AuthenticationState.INVALID_AUTHENTICATION
                _errorMessage.value = handleFirebaseAuthException(e)
            } catch (e: Exception) {
                _errorMessage.value = "An unexpected error occurred. Please try again later."
                Log.e("SharedViewModel", "Sign In Error: ", e)
            }
        }
    }

    /**
     * Attempts to sign up the user
     * @param email The user's email
     * @param password The user's password
     */
    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            try {
                accountService.signUp(email, password)
            } catch (e: FirebaseAuthException) {
                _authenticationState.value = AuthenticationState.INVALID_AUTHENTICATION
                _errorMessage.value = handleFirebaseAuthException(e)
            } catch (e: Exception) {
                _errorMessage.value = "An unexpected error occurred. Please try again later."
                Log.e("SharedViewModel", "Sign Up Error: ", e)
            }
        }
    }

    /**
     * Handles exceptions from Firebase authentication
     * @param e The FirebaseAuthException to handle
     * @return A string message describing the error
     */
    private fun handleFirebaseAuthException(e: FirebaseAuthException): String {
        return when (e.errorCode) {
            "ERROR_INVALID_EMAIL" -> "The email address is badly formatted."
            "ERROR_USER_DISABLED" -> "The user account has been disabled."
            "ERROR_USER_NOT_FOUND", "ERROR_WRONG_PASSWORD" -> "Invalid email or password."
            // Add other error codes as needed
            else -> "An unknown error occurred. Please try again."
        }
    }

    /**
     * Test connection to Firestore database
     */
    fun testFirestoreConnection() {
        viewModelScope.launch {
            try {
                FirebaseFirestore.getInstance().collection("testCollection")
                    .add(mapOf("testField" to "testValue"))
                    .addOnSuccessListener { documentReference ->
                        Log.d("SharedViewModel", "Document added with ID: ${documentReference.id}")
                        _errorMessage.value = "Test succeeded! Document added."
                    }
                    .addOnFailureListener { e ->
                        Log.w("SharedViewModel", "Error adding document", e)
                        _errorMessage.value = "Test failed: ${e.message}"
                    }
            } catch (e: Exception) {
                Log.e("SharedViewModel", "Test Firestore Connection Error: ", e)
                _errorMessage.value = "An unexpected error occurred. Please try again later."
            }
        }
    }

    /**
     * Signs the user out
     */
    fun signOut() {
        viewModelScope.launch {
            accountService.signOut()
        }
    }

    /**
     * Creates or updates a note
     * @param note The note to be created or updated
     */
    fun createOrUpdateNote(note: Note) {
        viewModelScope.launch {
            try {
                if (note.id.isEmpty()) {
                    storageService.createNote(note)
                } else {
                    storageService.updateNote(note)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to save note: ${e.message}"
                Log.e("SharedViewModel", "Error saving note", e)
            }
        }
    }

    /**
     * Deletes a note
     * @param noteId The ID of the note to be deleted
     */
    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            storageService.deleteNote(noteId)
        }
    }

    /**
     * Selects a note.
     * @param note The note to be selected
     */
    fun selectNote(note: Note) {
        _selectedNote.value = note
    }

    /**
     * Clears the currently selected note
     */
    fun clearSelectedNote() {
        _selectedNote.value = null
    }

}
/**
 * Enum representing the possible authentication states of a user
 */
enum class AuthenticationState {
    AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
}