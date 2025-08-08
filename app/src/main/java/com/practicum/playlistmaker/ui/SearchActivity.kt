package com.practicum.playlistmaker.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.notes.TracksAdapter
import com.practicum.playlistmaker.model.Track

class SearchActivity : AppCompatActivity() {
    private var query: String = QUERY_DEF
    lateinit var inputEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val backBtn = findViewById<TextView>(R.id.btn_back)
        val clearBtn = findViewById<ImageView>(R.id.iv_clear)
        inputEditText = findViewById(R.id.et_search)
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager


        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val tracks = loadTracks(this)
        val tracksAdapter = TracksAdapter(tracks)
        recyclerView.adapter = tracksAdapter

        backBtn.setOnClickListener {
            finish()
        }

        clearBtn.setOnClickListener {
            inputEditText.setText("")
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearBtn.visibility = clearButtonVisibility(s)
                if (clearBtn.visibility == View.GONE) inputMethodManager?.hideSoftInputFromWindow(
                    inputEditText.windowToken, 0
                )
            }

            override fun afterTextChanged(s: Editable?) {
                query = s.toString()
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(QUERY, query)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        query = savedInstanceState.getString(QUERY, QUERY_DEF)
        inputEditText.setText(query)
    }

    companion object {
        private const val QUERY = "QUERY"
        private const val QUERY_DEF = ""
    }
}

private fun clearButtonVisibility(s: CharSequence?): Int {
    return if (s.isNullOrEmpty()) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

private fun loadTracks(context: Context): List<Track> {
    val json = context.assets.open("tracks.json").bufferedReader().use { it.readText() }
    val type = object : TypeToken<List<Track>>() {}.type
    return Gson().fromJson(json, type)
}
