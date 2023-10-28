package com.example.c323_project_7_note.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.Timestamp

private const val TITLE_MAX_SIZE = 32
/**
 * Represents a note object in the application
 *
 * @property id Unique identifier of the note
 * @property title Title of the note
 * @property text Content text of the note
 * @property userId Identifier of the user who owns the note
 * @property noteSavedAt Timestamp indicating when the note was last saved
 */
data class Note(
    @DocumentId val id: String = "",
    val title: String = "",
    val text: String = "",
    val userId: String = "",
    @ServerTimestamp val noteSavedAt: Timestamp? = null
)
/**
 * Extracts a shortened title from the note's text. If the text is longer than 32 characters,
 * it will be truncated to 32 characters. Otherwise, the full text is used as the title
 *
 * @receiver The Note object
 * @return A string representing a shortened title
 */
fun Note.getTitle(): String {
    val isLongText = this.title.length > TITLE_MAX_SIZE
    val endRange = if (isLongText) TITLE_MAX_SIZE else this.text.length - 1
    return this.title.substring(IntRange(0, endRange))
}