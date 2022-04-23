package com.example.winenotes.database

import androidx.room.*


@Dao
interface NoteDao {
    @Query("SELECT * FROM note")
    fun getAllNotes(): List<Note>

   @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addNote(note: Note)

    @Update
    fun updateNote(note: Note)

    @Delete
    fun deleteNote(note: Note)

    @Query("DELETE FROM note")
    fun clearAllNotes()
}