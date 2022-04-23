package com.example.winenotes

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.winenotes.database.AppDatabase
import com.example.winenotes.database.Note
import com.example.winenotes.database.NoteDao
import com.example.winenotes.databinding.ActivityMainBinding
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MyAdapter
    private val notes = mutableListOf<Note>()
    private lateinit var db: AppDatabase
    private lateinit var dao: NoteDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(applicationContext)
        dao = db.notesDao()

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerview.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(applicationContext,layoutManager.getOrientation())
        binding.recyclerview.addItemDecoration(dividerItemDecoration)

        adapter = MyAdapter()
        binding.recyclerview.adapter = adapter

        loadAllNotes()
    }
private fun loadAllNotes() {
    CoroutineScope(Dispatchers.IO).launch {
        val results = dao.getAllNotes()

        withContext(Dispatchers.Main) {
            notes.clear()
            notes.addAll(results)
            adapter.notifyDataSetChanged()
        }
    }
}

    private val startForAddResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                loadAllNotes()
            }
        }

    private val startForUpdateResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                loadAllNotes()
            }
        }

    private fun addNewNote() {
        val intent = Intent(applicationContext, NoteActivity::class.java)
        startForAddResult.launch(intent)
    }



//    private fun deleteAllNotes(){
//        val builder = AlertDialog.Builder(this)
//            .setTitle("Confirm delete")
//            .setMessage("Are you sure you want to delete all data?")
//            .setNegativeButton(android.R.string.cancel, null)
//            .setPositiveButton(android.R.string.ok) {
//                    dialogInterface, whichButton ->
//
//                CoroutineScope(Dispatchers.IO).launch {
//
//                        getDatabase(applicationContext)
//                        .NotesDao()
//                        .deleteAllPeople()
//
//                    // alternative - reload the whole database
//                    // good only for small databases
//                    loadAllNotes()
//
//                }
//
//            }
//        builder.show()
//    }

    inner class MyViewHolder(view : View) : RecyclerView.ViewHolder(view),View.OnClickListener, View.OnLongClickListener {
        val title: TextView
        val decription: TextView

        init {
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
            title = view.findViewById(R.id.txtTitle)
            decription = view.findViewById(R.id.txtDescription)
        }

        override fun onClick(p0: View?) {
            val intent = Intent(applicationContext, NoteActivity::class.java)
            intent.putExtra("note", notes[adapterPosition])
            startForUpdateResult.launch(intent)
        }

        override fun onLongClick(p0: View?): Boolean {
            val note = notes[adapterPosition]
            notes.removeAt(adapterPosition)
            adapter.notifyItemRemoved(adapterPosition)

            CoroutineScope(Dispatchers.IO).launch {
                dao.deleteNote(note)
            }

            return true
        }
    }

    inner class MyAdapter : RecyclerView.Adapter<MainActivity.MyViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = layoutInflater.inflate(R.layout.item_view,parent,false)
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val note = notes[position]
            holder.title.text = note.noteName
            holder.decription.text = note.wineryNotes
        }

        override fun getItemCount(): Int {
            return notes.size
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.notes_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.addNote){
           addNewNote()
            return true
        }else if(item.itemId == R.id.clearNotes){
            CoroutineScope(Dispatchers.IO).launch {
                dao.clearAllNotes()
                loadAllNotes()
            }

            return true

        }
        return super.onOptionsItemSelected(item)
    }


}

