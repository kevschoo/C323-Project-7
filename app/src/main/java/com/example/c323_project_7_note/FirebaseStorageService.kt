package com.example.c323_project_7_note

import android.util.Log
import com.example.c323_project_7_note.model.Note
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.*

class FirebaseStorageService : StorageService {

    private val firestore: FirebaseFirestore = Firebase.firestore
    private val userId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    /**
     * Gets a Flow of list of notes for a specific user
     *
     * @param userId Unique identifier of the user
     * @return Flow emitting lists of notes
     */
    override fun notes(userId: String): Flow<List<Note>> {
        return firestore.collection("notes")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.documents.map { document ->
                    document.toObject(Note::class.java) ?: Note()
                }
            }
    }

    /**
     * Creates a new note in the storage
     *
     * @param note The note to be created
     */
    override suspend fun createNote(note: Note) {
        try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: throw IllegalStateException("User not logged in")
            val noteWithUserId = note.copy(userId = userId)
            firestore.collection("notes").add(noteWithUserId).await()
        } catch (e: Exception) {
            Log.e("FirebaseStorageService", "Error creating note", e)
            throw e
        }
    }

    /**
     * Updates an existing note in the storage
     *
     * @param note The note to be updated
     */
    override suspend fun updateNote(note: Note) {
        note.id.takeIf { it.isNotBlank() }?.let { noteId ->
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: throw IllegalStateException("User not logged in")
            val noteWithUserId = note.copy(userId = userId)
            firestore.collection("notes").document(noteId).set(noteWithUserId).await()
        }
    }

    /**
     * Reads a note by its unique identifier
     *
     * @param noteId Unique identifier of the note
     * @return The requested note or null if it does not exist
     */
    override suspend fun readNote(noteId: String): Note? {
        return firestore.collection("notes").document(noteId).get().await().toObject(Note::class.java)
    }

    /**
     * Deletes a note by its unique identifier
     *
     * @param noteId Unique identifier of the note to be deleted
     */
    override suspend fun deleteNote(noteId: String) {
        firestore.collection("notes").document(noteId).delete().await()
    }
}