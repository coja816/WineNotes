package com.example.winenotes.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*

@Entity
@Parcelize
data class Note(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "note_name") val noteName: String,
    @ColumnInfo(name = "winery_notes") val wineryNotes: String,
    @ColumnInfo(name = "winery_date") val createdDate: String
) : Parcelable {
    override fun toString(): String {
        return "${noteName} ${wineryNotes}"
    }
}