package com.raminarmanfar.sampleroomdatabase

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddEditNoteActivity : AppCompatActivity() {
	companion object {
		val EXTRA_ID = "com.raminarmanfar.sampleroomdatabase.EXTRA_ID"
		val EXTRA_TITLE = "com.raminarmanfar.sampleroomdatabase.EXTRA_TITLE"
		val EXTRA_DESCRIPTION = "com.raminarmanfar.sampleroomdatabase.EXTRA_DESCRIPTION"
		val EXTRA_PRIORITY = "com.raminarmanfar.sampleroomdatabase.EXTRA_PRIORITY"
	}


	private lateinit var editTextTitle: EditText
	private lateinit var editTextDescription: EditText
	private lateinit var numberPickerPriority: NumberPicker

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_add_note)

		editTextTitle = findViewById(R.id.edit_text_title)
		editTextDescription = findViewById(R.id.edit_text_description)
		numberPickerPriority = findViewById(R.id.number_picker_priority)
		numberPickerPriority.minValue = 1
		numberPickerPriority.maxValue = 10

		supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_close)

		if (intent.hasExtra(EXTRA_ID)) {
			title = "Edit Note"
			editTextTitle.setText(intent.getStringExtra(EXTRA_TITLE))
			editTextDescription.setText(intent.getStringExtra(EXTRA_DESCRIPTION))
			numberPickerPriority.value = intent.getIntExtra(EXTRA_PRIORITY, 1)
		} else {
			title = "Add Note"
		}
	}

	private fun saveNote() {
		val title = editTextTitle.text.toString()
		val description = editTextDescription.text.toString()
		val priority = numberPickerPriority.value

		if(title.trim().isEmpty() || description.trim().isEmpty()) {
			Toast.makeText(this, "Please insert a title and description.", Toast.LENGTH_LONG).show()
			return
		}
		val data = Intent()
		data.putExtra(EXTRA_TITLE, title)
		data.putExtra(EXTRA_DESCRIPTION, description)
		data.putExtra(EXTRA_PRIORITY, priority)

		val id = intent.getIntExtra(EXTRA_ID, 1)
		if (id != -1) {
			data.putExtra(EXTRA_ID, id)
		}

		setResult(Activity.RESULT_OK, data)
		finish()
	}

	@Override
	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		val menuInflater: MenuInflater = menuInflater
		menuInflater.inflate(R.menu.add_note_menu, menu)
		return true
	}

	@Override
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.save_note -> {
				saveNote()
				true
			}
			else -> super.onOptionsItemSelected(item)
		}

	}
}
