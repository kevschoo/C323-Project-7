package com.example.c323_project_7_note

import com.example.c323_project_7_note.model.Note
import kotlinx.coroutines.flow.Flow

/**
 * Service interface for handling storage operations related to notes
 */
interface StorageService {
    /**
     * Gets a Flow of list of notes for a specific user
     *
     * @param userId Unique identifier of the user
     * @return Flow emitting lists of notes
     */
    fun notes(userId: String): Flow<List<Note>>

    /**
     * Creates a new note in the storage
     *
     * @param note The note to be created
     */
    suspend fun createNote(note: Note)

    /**
     * Reads a note by its unique identifier
     *
     * @param noteId Unique identifier of the note
     * @return The requested note or null if it does not exist
     */
    suspend fun readNote(noteId: String): Note?

    /**
     * Updates an existing note in the storage
     *
     * @param note The note to be updated
     */
    suspend fun updateNote(note: Note)

    /**
     * Deletes a note by its unique identifier
     *
     * @param noteId Unique identifier of the note to be deleted
     */
    suspend fun deleteNote(noteId: String)
}