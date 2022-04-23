package com.example.winenotes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.winenotes.database.AppDatabase
import com.example.winenotes.database.Note
import com.example.winenotes.databinding.ActivityMainBinding
import com.example.winenotes.databinding.ActivityNoteBinding
import kotlinx.android.synthetic.main.activity_note.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class NoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoteBinding
    private var note: Note? = null
    private var update = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        note = intent.getParcelableExtra("note")

        note?.let {
            update = true
            binding.edtTitle.setText(it.noteName)
            binding.edtDescription.setText(it.wineryNotes)
            binding.txtDate.text = it.createdDate
        }

        binding.btnAdd.setOnClickListener {
            if (update) {
                val note = Note(
                    noteName = edtTitle.text.toString(),
                    wineryNotes = edtDescription.text.toString(),
                    createdDate = note!!.createdDate
                )
                note.id = this.note!!.id
                val db = AppDatabase.getDatabase(applicationContext)
                val dao = db.notesDao()
                CoroutineScope(Dispatchers.IO).launch {
                    dao.updateNote(note)

                    withContext(Dispatchers.Main) {
                        setResult(RESULT_OK, Intent())
                        finish()
                    }
                }
            } else {
                val note = Note(
                    noteName = edtTitle.text.toString(),
                    wineryNotes = edtDescription.text.toString(),
                    createdDate = formatDate(Date())
                )
                val db = AppDatabase.getDatabase(applicationContext)
                val dao = db.notesDao()
                CoroutineScope(Dispatchers.IO).launch {
                    dao.addNote(note)
                    withContext(Dispatchers.Main) {
                        setResult(RESULT_OK, Intent())
                        finish()
                    }
                }
            }
        }
    }

    private fun formatDate(date: Date): String {
        val databaseDateFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        databaseDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val dateString: String = databaseDateFormat.format(date)
        Log.i("TIME", "Time is: ${dateString}")

        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        parser.setTimeZone(TimeZone.getTimeZone("UTC"))
        val dateInDatabase: Date = parser.parse(dateString)
        val displayFormat = SimpleDateFormat("HH:mm a MM/yyyy ")
        val displayString: String = displayFormat.format(dateInDatabase)
        Log.i("TIME", "Time is NOW: ${displayString}")

        return displayString
    }
}