package com.practicum.playlistmaker.ui.search.activity

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
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.ui.search.adapters.TracksAdapter
import com.practicum.playlistmaker.ui.search.view_model.HistoryState
import com.practicum.playlistmaker.ui.search.view_model.SearchState
import com.practicum.playlistmaker.ui.search.view_model.TracksViewModel
import com.practicum.playlistmaker.ui.audioplayer.activity.AudioPlayerActivity
import com.practicum.playlistmaker.ui.mappers.toParcelable
import com.practicum.playlistmaker.utils.applySystemBarsPadding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {
    private var query: String = QUERY_DEF
    private lateinit var binding: ActivitySearchBinding
    private lateinit var tracksAdapter: TracksAdapter
    private var isClickAllowed = true
    private var mainThreadHandler = Handler(Looper.getMainLooper())
    private val viewModel: TracksViewModel by viewModel()

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

                viewModel.onTrackClicked(track)
            }
        }


        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        binding.recyclerView.adapter = tracksAdapter

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.ivClear.setOnClickListener {
            binding.apply {
                etSearch.setText("")
                llPlaceholder.visibility = View.GONE
            }
        }

        binding.btnRenew.setOnClickListener {
            viewModel.onSearchRequested(binding.etSearch.text.toString())
            inputMethodManager?.hideSoftInputFromWindow(
                binding.etSearch.windowToken, 0
            )
        }

        binding.etSearch.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && binding.etSearch.text.isBlank()) {
                viewModel.onShowTracksHistory()
            }
        }

        binding.btnClearHistory.setOnClickListener {
            viewModel.onDeleteTracksHistory()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchDebounce(s.toString())

                binding.ivClear.visibility = clearButtonVisibility(s)
                if (binding.ivClear.isGone) inputMethodManager?.hideSoftInputFromWindow(
                    binding.etSearch.windowToken, 0
                )
                if (binding.etSearch.hasFocus() && s?.isEmpty() == true) {
                    viewModel.onShowTracksHistory()
                } else hideTracksHistory()
            }

            override fun afterTextChanged(s: Editable?) {
                query = s.toString()
            }
        }
        binding.etSearch.addTextChangedListener(simpleTextWatcher)

        viewModel.observeHistoryState().observe(this) {
            renderHistory(it)
        }

        viewModel.observeSearchStateLiveData().observe(this) {
            renderSearch(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainThreadHandler.removeCallbacksAndMessages(null)
    }

    fun showLoader() {
        binding.apply {
            recyclerViewWrapper.visibility = View.GONE
            llPlaceholder.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        }
    }

    fun hideLoader() {
        binding.apply {
            recyclerViewWrapper.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }
    }

    fun showFoundTracks(foundTracks: List<Track>) {
        hideLoader()

        binding.llPlaceholder.visibility = View.GONE
        tracksAdapter.submitList(foundTracks.toList())
    }

    fun showEmptyState(message: String) {
        hideLoader()
        tracksAdapter.submitList(listOf())

        binding.apply {
            ivPlaceholder.setImageResource(R.drawable.ic_nothing_found_120)
            tvServerResponse.text = message
            btnRenew.visibility = View.GONE
            llPlaceholder.visibility = View.VISIBLE
        }
    }

    fun showErrorState(
        message: String
    ) {
        hideLoader()
        tracksAdapter.submitList(listOf())

        binding.apply {
            ivPlaceholder.setImageResource(R.drawable.ic_network_issues_120)
            tvServerResponse.text = message

            btnRenew.visibility = View.VISIBLE
            llPlaceholder.visibility = View.VISIBLE
        }
    }


    fun showTracksHistory(receivedTracks: List<Track>) {
        tracksAdapter.submitList(receivedTracks)

        binding.apply {
            llPlaceholder.visibility = View.GONE
            tvYouSearched.visibility = View.VISIBLE
            btnClearHistory.visibility = View.VISIBLE
        }
    }

    fun hideTracksHistory() {
        tracksAdapter.submitList(listOf())

        binding.apply {
            tvYouSearched.visibility = View.GONE
            btnClearHistory.visibility = View.GONE
        }
    }

    fun renderHistory(state: HistoryState) {
        when (state) {
            is HistoryState.Content -> showTracksHistory(state.tracks)
            is HistoryState.Hidden -> hideTracksHistory()
        }
    }

    fun renderSearch(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showLoader()
            is SearchState.Empty -> showEmptyState(state.message)
            is SearchState.Connection -> showErrorState(state.message)
            is SearchState.Error -> showErrorState(state.message)
            is SearchState.Content -> showFoundTracks(state.tracks)
        }
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

