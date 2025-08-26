package com.practicum.playlistmaker.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.api.NetworkModule
import com.practicum.playlistmaker.ui.notes.TracksAdapter
import com.practicum.playlistmaker.data.model.Track
import com.practicum.playlistmaker.data.model.TracksResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.core.view.isGone


class SearchActivity : AppCompatActivity() {
    private var query: String = QUERY_DEF
    private val iTunesService = NetworkModule.iTunesService
    private lateinit var inputEditText: EditText
    private lateinit var serverResponsePlaceholder: LinearLayout
    private lateinit var serverResponseImagePlaceholder: ImageView
    private lateinit var serverResponseTextView: TextView
    private lateinit var renewButton: Button
    private val tracks = ArrayList<Track>()
    val tracksAdapter = TracksAdapter(tracks)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val backBtn = findViewById<TextView>(R.id.btn_back)
        val clearBtn = findViewById<ImageView>(R.id.iv_clear)
        serverResponsePlaceholder = findViewById(R.id.ll_placeholder)
        serverResponseImagePlaceholder = findViewById(R.id.iv_placeholder)
        serverResponseTextView = findViewById(R.id.tv_server_response)
        renewButton = findViewById(R.id.btn_renew)
        inputEditText = findViewById(R.id.et_search)

        serverResponsePlaceholder.visibility = View.GONE

        val inputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.adapter = tracksAdapter

        backBtn.setOnClickListener {
            finish()
        }

        clearBtn.setOnClickListener {
            inputEditText.setText("")
            tracks.clear()
            tracksAdapter.submitList(ArrayList(tracks))
            hideSearchState()
        }

        renewButton.setOnClickListener {
            searchTracks()
            inputMethodManager?.hideSoftInputFromWindow(
                inputEditText.windowToken, 0
            )
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTracks()
                inputMethodManager?.hideSoftInputFromWindow(
                    inputEditText.windowToken, 0
                )
                true
            } else {
                false
            }
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearBtn.visibility = clearButtonVisibility(s)
                if (clearBtn.isGone) inputMethodManager?.hideSoftInputFromWindow(
                    inputEditText.windowToken, 0
                )
            }

            override fun afterTextChanged(s: Editable?) {
                query = s.toString()
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)
    }

    private fun showEmptyState() {
        tracks.clear()
        tracksAdapter.submitList(ArrayList(tracks))

        serverResponseImagePlaceholder.setImageResource(R.drawable.ic_nothing_found_120)
        serverResponseTextView.text = getString(R.string.nothing_found)
        renewButton.visibility = View.GONE
        serverResponsePlaceholder.visibility = View.VISIBLE
    }

    private fun showErrorState(
        errorMessage: String?,
        isConnectionError: Boolean
    ) {
        tracks.clear()
        tracksAdapter.submitList(ArrayList(tracks))

        serverResponseImagePlaceholder.setImageResource(R.drawable.ic_network_issues_120)
        serverResponseTextView.text =
            if (isConnectionError) {
                String.format(getString(R.string.connection_failure), errorMessage)
            } else {
                String.format(getString(R.string.response_error), errorMessage)
            }

        renewButton.visibility = View.VISIBLE
        serverResponsePlaceholder.visibility = View.VISIBLE
    }

    private fun hideSearchState() {
        serverResponsePlaceholder.visibility = View.GONE
    }

    private fun searchTracks() {

        if (inputEditText.text.isNotBlank()) {
            iTunesService.searchTracks(inputEditText.text.toString()).enqueue(object :
                Callback<TracksResponse> {
                override fun onResponse(
                    call: Call<TracksResponse?>,
                    response: Response<TracksResponse?>
                ) {
                    if (response.code() == 200) {
                        tracks.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            tracks.addAll(response.body()?.results ?: arrayListOf())
                            tracksAdapter.submitList(ArrayList(tracks))
                        }
                        if (tracks.isEmpty()) {
                            showEmptyState()
                        } else {
                            hideSearchState()
                        }
                    } else {
                        showErrorState(
                            response.code().toString(),
                            false
                        )
                    }
                }

                override fun onFailure(call: Call<TracksResponse?>, t: Throwable) {
                    showErrorState(t.message.toString(), true)
                }
            })
        }
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


//private fun loadTracks(context: Context): List<Track> {
//    val json = context.assets.open("tracks.json").bufferedReader().use { it.readText() }
//    val type = object : TypeToken<List<Track>>() {}.type
//    return Gson().fromJson(json, type)
//}
