package com.practicum.playlistmaker.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.api.NetworkModule
import com.practicum.playlistmaker.ui.notes.TracksAdapter
import com.practicum.playlistmaker.data.model.Track
import com.practicum.playlistmaker.data.model.TracksResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.core.view.isGone
import com.practicum.playlistmaker.App.Companion.SHARED_PREFS_FILE
import com.practicum.playlistmaker.data.storage.SearchHistory
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.utils.applySystemBarsPadding

class SearchActivity : AppCompatActivity() {
    private var query: String = QUERY_DEF
    private lateinit var binding: ActivitySearchBinding
    private val iTunesService = NetworkModule.iTunesService
    private val tracks = ArrayList<Track>()
    private lateinit var searchHistory: SearchHistory
    private lateinit var tracksAdapter: TracksAdapter
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.applySystemBarsPadding()

        sharedPrefs = getSharedPreferences(SHARED_PREFS_FILE, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPrefs)
        searchHistory.readTracksHistory()
        tracksAdapter = TracksAdapter { track ->
            val displayIntent = Intent(this, AudioPlayerActivity::class.java)
            displayIntent.putExtra(AudioPlayerActivity.INTENT_EXTRA_KEY, track)
            startActivity(displayIntent)

            searchHistory.addTrackInTracksHistory(track)
            searchHistory.saveTracksHistory()
        }

        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        binding.recyclerView.adapter = tracksAdapter

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.ivClear.setOnClickListener {
            binding.etSearch.setText("")
            hideSearchState()
        }

        binding.btnRenew.setOnClickListener {
            searchTracks()
            inputMethodManager?.hideSoftInputFromWindow(
                binding.etSearch.windowToken, 0
            )
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTracks()
                inputMethodManager?.hideSoftInputFromWindow(
                    binding.etSearch.windowToken, 0
                )
                true
            } else {
                false
            }
        }

        binding.etSearch.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && binding.etSearch.text.isBlank() && (searchHistory.getTracksHistoryCopy()
                    .isNotEmpty())
            ) {
                showTracksHistory()
            } else {
                hideTracksHistory()
            }
        }

        binding.btnClearHistory.setOnClickListener {
            searchHistory.clearTracksHistory()
            hideTracksHistory()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.ivClear.visibility = clearButtonVisibility(s)
                if (binding.ivClear.isGone) inputMethodManager?.hideSoftInputFromWindow(
                    binding.etSearch.windowToken, 0
                )
                if (binding.etSearch.hasFocus() && s?.isEmpty() == true && searchHistory.getTracksHistoryCopy()
                        .isNotEmpty()
                ) {
                    showTracksHistory()
                } else {
                    hideTracksHistory()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                query = s.toString()
            }
        }
        binding.etSearch.addTextChangedListener(simpleTextWatcher)
    }

    private fun showEmptyState() {
        tracks.clear()
        tracksAdapter.submitList(tracks.toList())

        binding.ivPlaceholder.setImageResource(R.drawable.ic_nothing_found_120)
        binding.tvServerResponse.text = getString(R.string.nothing_found)
        binding.btnRenew.visibility = View.GONE
        binding.llPlaceholder.visibility = View.VISIBLE
    }

    private fun showErrorState(
        errorMessage: String?, isConnectionError: Boolean
    ) {
        tracks.clear()
        tracksAdapter.submitList(tracks.toList())

        binding.ivPlaceholder.setImageResource(R.drawable.ic_network_issues_120)
        binding.tvServerResponse.text = if (isConnectionError) {
            String.format(getString(R.string.connection_failure), errorMessage)
        } else {
            String.format(getString(R.string.response_error), errorMessage)
        }

        binding.btnRenew.visibility = View.VISIBLE
        binding.llPlaceholder.visibility = View.VISIBLE
    }

    private fun hideSearchState() {
        binding.llPlaceholder.visibility = View.GONE
    }

    private fun searchTracks() {

        if (binding.etSearch.text.isNotBlank()) {
            iTunesService.searchTracks(binding.etSearch.text.toString())
                .enqueue(object : Callback<TracksResponse> {
                    override fun onResponse(
                        call: Call<TracksResponse?>, response: Response<TracksResponse?>
                    ) {
                        if (response.code() == 200) {
                            tracks.clear()
                            if (response.body()?.results?.isNotEmpty() == true) {
                                tracks.addAll(response.body()?.results ?: arrayListOf())
                                tracksAdapter.submitList(tracks.toList())
                            }
                            if (tracks.isEmpty()) {
                                showEmptyState()
                            } else {
                                hideSearchState()
                            }
                        } else {
                            showErrorState(
                                response.code().toString(), false
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
        binding.etSearch.setText(query)
    }

    private fun showTracksHistory() {
        tracks.clear()
        tracks.addAll(searchHistory.getTracksHistoryCopy().reversed())
        tracksAdapter.submitList(tracks.toList())
        binding.tvYouSearched.visibility = View.VISIBLE
        binding.btnClearHistory.visibility = View.VISIBLE
    }

    private fun hideTracksHistory() {
        tracks.clear()
        tracksAdapter.submitList(tracks.toList())
        binding.tvYouSearched.visibility = View.GONE
        binding.btnClearHistory.visibility = View.GONE
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

