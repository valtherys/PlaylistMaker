package com.practicum.playlistmaker.ui.search.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager

import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track
import androidx.core.view.isGone
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.ui.audioplayer.fragment.AudioPlayerFragment
import com.practicum.playlistmaker.ui.search.adapters.TracksAdapter
import com.practicum.playlistmaker.ui.search.view_model.TracksState
import com.practicum.playlistmaker.ui.search.view_model.TracksViewModel
import com.practicum.playlistmaker.ui.common.BindingFragment
import com.practicum.playlistmaker.ui.mappers.toParcelable
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : BindingFragment<FragmentSearchBinding>() {
    private var query: String = QUERY_DEF
    private lateinit var tracksAdapter: TracksAdapter
    private var isClickAllowed = true
    private var mainThreadHandler = Handler(Looper.getMainLooper())
    private val viewModel: TracksViewModel by viewModel()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tracksAdapter = TracksAdapter { track ->
            if (clickDebounce()) {
                findNavController().navigate(
                    R.id.action_searchFragment_to_audioPlayerFragment,
                    AudioPlayerFragment.createArgs(track.toParcelable())
                )

                viewModel.onTrackClicked(track)
            }
        }
        query = savedInstanceState?.getString(QUERY) ?: QUERY_DEF
        binding.etSearch.setText(query)

        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.recyclerView.adapter = tracksAdapter

        binding.ivClear.setOnClickListener {
            viewModel.searchResultsDebounce()
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

        viewModel.observeTracksStateLiveData().observe(viewLifecycleOwner) {
            render(it)
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
        hideLoader()
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

    fun render(state: TracksState) {
        when (state) {
            is TracksState.Loading -> showLoader()
            is TracksState.Empty -> showEmptyState(state.message)
            is TracksState.Connection -> showErrorState(state.message)
            is TracksState.Error -> showErrorState(state.message)
            is TracksState.SearchContent -> showFoundTracks(state.tracks)
            is TracksState.HiddenHistory -> hideTracksHistory()
            is TracksState.HistoryContent -> showTracksHistory(state.tracks)
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

