package com.practicum.playlistmaker

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.util.Log
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {

    var stringValue = ""
    lateinit var inputEditText: EditText
    lateinit var back: ImageView
    lateinit var clearButton: ImageView
    val trackList: MutableList<Track> = ArrayList()

    companion object {
        const val SEARCH_STRING = "SEARCH_STRING"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        fillTrackList()
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val trackAdapter = TrackAdapter(trackList)
        recyclerView.adapter = trackAdapter

        inputEditText = findViewById<EditText>(R.id.edit_text)
        back = findViewById<ImageView>(R.id.back)
        clearButton = findViewById<ImageView>(R.id.clear_icon)

        back.setOnClickListener {
            this.finish()
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            val view: View? = this.currentFocus
            if (view != null) {
                val inputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0)
            }
        }

        inputEditText.requestFocus()

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                stringValue = inputEditText.text.toString()
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_STRING, stringValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        stringValue = savedInstanceState.getString(SEARCH_STRING,"")
        inputEditText.setText(stringValue)
    }

    private fun fillTrackList() {
        for(i in 1..5) {

            val trackNameID = resources.getIdentifier("mock_track_name_" + i, "string", getPackageName());
            val trackArtistID = resources.getIdentifier("mock_track_artist_" + i, "string", getPackageName());
            val trackDurID = resources.getIdentifier("mock_track_dur_" + i, "string", getPackageName());
            val trackURLID = resources.getIdentifier("mock_track_url_" + i, "string", getPackageName());

            val track = Track(
                getString(trackNameID),
                getString(trackArtistID),
                getString(trackDurID),
                getString(trackURLID)
            )

            trackList.add(track)
        }


    }
}