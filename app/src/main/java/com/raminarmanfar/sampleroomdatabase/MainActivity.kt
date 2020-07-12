package com.raminarmanfar.sampleroomdatabase

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {
	val ADD_NOTE_REQUEST = 1
	val EDIT_NOTE_REQUEST = 2

	private lateinit var noteViewModel: NoteViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		val btnAddNotes = findViewById<FloatingActionButton>(R.id.btn_add_note)
		btnAddNotes.setOnClickListener {
			val intent = Intent(this@MainActivity, AddEditNoteActivity::class.java)
			startActivityForResult(intent, ADD_NOTE_REQUEST)
		}

		val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
		recyclerView.layoutManager = LinearLayoutManager(this)
		recyclerView.setHasFixedSize(true)

		val noteAdapter = NoteAdapter()
		recyclerView.adapter = noteAdapter

		noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)
		noteViewModel.allNotes.observe(
			this,
			Observer { notes ->
				noteAdapter.submitList(notes)
				// Toast.makeText(this@MainActivity, "onChanged", Toast.LENGTH_LONG).show()
			})

		val mIth = ItemTouchHelper(
			object : ItemTouchHelper.SimpleCallback(0,  ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
				override fun onMove(recyclerView: RecyclerView,	viewHolder: ViewHolder, target: ViewHolder): Boolean {
					val fromPos = viewHolder.adapterPosition
					val toPos = target.adapterPosition
					// move item in `fromPos` to `toPos` in adapter.
					return true // true if moved, false otherwise
				}

				override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
					noteViewModel.delete(noteAdapter.getNoteAt(viewHolder.adapterPosition))
					Toast.makeText(this@MainActivity, "Note has been deleted.", Toast.LENGTH_LONG).show()
				}
			}).attachToRecyclerView(recyclerView)

		noteAdapter.setOnItemClickListener(NoteAdapter.OnItemClickListener { note ->
			val intent = Intent(this@MainActivity, AddEditNoteActivity::class.java)
			intent.putExtra(AddEditNoteActivity.EXTRA_ID, note.id)
			intent.putExtra(AddEditNoteActivity.EXTRA_TITLE, note.title)
			intent.putExtra(AddEditNoteActivity.EXTRA_DESCRIPTION, note.description)
			intent.putExtra(AddEditNoteActivity.EXTRA_PRIORITY, note.priority)
			startActivityForResult(intent, EDIT_NOTE_REQUEST)
		})
	}

	@Override
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (resultCode == Activity.RESULT_OK) {
			val title = data!!.getStringExtra(AddEditNoteActivity.EXTRA_TITLE)
			val description = data!!.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION)
			val priority = data!!.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY, 1)
			val note = Note(title, description, priority)

			when (requestCode) {
				ADD_NOTE_REQUEST -> {
					noteViewModel.insert(note)
					Toast.makeText(this, "New note saved.", Toast.LENGTH_LONG).show()
				}
				EDIT_NOTE_REQUEST -> {
					val id = data!!.getIntExtra(AddEditNoteActivity.EXTRA_ID, 1)
					if (id == -1) {
						Toast.makeText(this, "Note can't be updated.", Toast.LENGTH_LONG).show()
						return
					}
					note.id = id
					noteViewModel.update(note)
					Toast.makeText(this, "Note updated.", Toast.LENGTH_LONG).show()
				}
			}
		} else {
			Toast.makeText(this, "Note NOT saved!!", Toast.LENGTH_LONG).show()
		}
	}

	@Override
	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.main_menu, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.delete_all_notes -> {
				noteViewModel.deleteAllNotes()
				Toast.makeText(this, "All notes deleted.", Toast.LENGTH_LONG).show()
				true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}
}
