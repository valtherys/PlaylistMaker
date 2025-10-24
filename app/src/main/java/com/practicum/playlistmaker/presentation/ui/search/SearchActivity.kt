package com.practicum.playlistmaker.presentation.ui.search

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track
import androidx.core.view.isGone
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.presentation.TracksAdapter
import com.practicum.playlistmaker.presentation.ui.audioplayer.AudioPlayerActivity
import com.practicum.playlistmaker.presentation.ui.mappers.toParcelable
import com.practicum.playlistmaker.utils.applySystemBarsPadding

class SearchActivity : AppCompatActivity(), SearchView, SearchHistoryView {
    private var query: String = QUERY_DEF
    private lateinit var binding: ActivitySearchBinding
    private val tracks = ArrayList<Track>()
    private lateinit var tracksAdapter: TracksAdapter
    private var isClickAllowed = true
    private var mainThreadHandler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { searchTracks() }
    private val presenter = Creator.provideTracksPresenter()
    private val tracksHistoryPresenter = Creator.provideTracksHistoryPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.applySystemBarsPadding()

        tracksAdapter = TracksAdapter { track ->
            if (clickDebounce()) {
                val trackParcelable = track.toParcelable()
                val displayIntent = Intent(this, AudioPlayerActivity::class.java)
                displayIntent.putExtra(
                    AudioPlayerActivity.Companion.INTENT_EXTRA_KEY,
                    trackParcelable
                )
                startActivity(displayIntent)

                tracksHistoryPresenter.onSaveTrackInHistory(track)
            }
        }
        tracksHistoryPresenter.onReadTracksHistory()

        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        binding.recyclerView.adapter = tracksAdapter

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.ivClear.setOnClickListener {
            binding.etSearch.setText("")
            binding.llPlaceholder.visibility = View.GONE
        }

        binding.btnRenew.setOnClickListener {
            searchTracks()
            inputMethodManager?.hideSoftInputFromWindow(
                binding.etSearch.windowToken, 0
            )
        }

        binding.etSearch.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && binding.etSearch.text.isBlank()) {
                tracksHistoryPresenter.onShowTracksHistory()
            } else {
                hideTracksHistory()
            }
        }

        binding.btnClearHistory.setOnClickListener {
            tracksHistoryPresenter.onDeleteTracksHistory()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchDebounce()

                binding.ivClear.visibility = clearButtonVisibility(s)
                if (binding.ivClear.isGone) inputMethodManager?.hideSoftInputFromWindow(
                    binding.etSearch.windowToken, 0
                )
                if (binding.etSearch.hasFocus() && s?.isEmpty() == true) {
                    tracksHistoryPresenter.onShowTracksHistory()
                } else hideTracksHistory()
            }

            override fun afterTextChanged(s: Editable?) {
                query = s.toString()
            }
        }
        binding.etSearch.addTextChangedListener(simpleTextWatcher)
    }

    override fun onStart() {
        super.onStart()
        presenter.attachView(this)
        tracksHistoryPresenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        presenter.detachView()
        tracksHistoryPresenter.detachView()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainThreadHandler.removeCallbacks(searchRunnable)
    }

    override fun showLoader() {
        runOnUiThread {
            binding.recyclerViewWrapper.visibility = View.GONE
            binding.llPlaceholder.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        }
    }

    override fun hideLoader() {
        runOnUiThread {
            binding.recyclerViewWrapper.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun showFoundTracks(foundTracks: ArrayList<Track>) {
        runOnUiThread {
            binding.llPlaceholder.visibility = View.GONE
            tracks.addAll(foundTracks)
            tracksAdapter.submitList(tracks.toList())
        }
    }

    override fun showEmptyState() {
        runOnUiThread {
            tracksAdapter.submitList(tracks.toList())

            binding.ivPlaceholder.setImageResource(R.drawable.ic_nothing_found_120)
            binding.tvServerResponse.text = getString(R.string.nothing_found)
            binding.btnRenew.visibility = View.GONE
            binding.llPlaceholder.visibility = View.VISIBLE
        }
    }

    override fun showErrorState(
        errorMessage: String, isConnectionError: Boolean
    ) {
        runOnUiThread {
            tracksAdapter.submitList(tracks.toList())

            binding.ivPlaceholder.setImageResource(R.drawable.ic_network_issues_120)
            binding.tvServerResponse.text = if (isConnectionError) {
                String.format(getString(R.string.connection_failure))
            } else {
                String.format(getString(R.string.response_error), errorMessage)
            }

            binding.btnRenew.visibility = View.VISIBLE
            binding.llPlaceholder.visibility = View.VISIBLE
        }
    }

    override fun clearTracks() {
        runOnUiThread { tracks.clear() }
    }

    private fun searchTracks() {
        presenter.onSearchRequested(binding.etSearch.text.toString())
    }

    override fun showTracksHistory(receivedTracks: ArrayList<Track>) {
        tracks.addAll(receivedTracks)
        tracksAdapter.submitList(tracks.toList())
        binding.tvYouSearched.visibility = View.VISIBLE
        binding.btnClearHistory.visibility = View.VISIBLE
    }

    override fun hideTracksHistory() {
        clearTracks()
        tracksAdapter.submitList(tracks.toList())
        binding.tvYouSearched.visibility = View.GONE
        binding.btnClearHistory.visibility = View.GONE
    }

    private fun searchDebounce() {
        mainThreadHandler.removeCallbacks(searchRunnable)
        mainThreadHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            mainThreadHandler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
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

    companion object {
        private const val QUERY = "QUERY"
        private const val QUERY_DEF = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}

private fun clearButtonVisibility(s: CharSequence?): Int {
    return if (s.isNullOrEmpty()) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

