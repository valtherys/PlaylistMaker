package com.practicum.playlistmaker.ui.search.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isGone
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.audioplayer.fragment.AudioPlayerFragment
import com.practicum.playlistmaker.ui.common.BindingFragment
import com.practicum.playlistmaker.ui.mappers.toParcelable
import com.practicum.playlistmaker.ui.common.adapters.TracksAdapter
import com.practicum.playlistmaker.ui.search.view_model.TracksState
import com.practicum.playlistmaker.ui.search.view_model.TracksViewModel
import com.practicum.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : BindingFragment<FragmentSearchBinding>() {
    private var query: String = QUERY_DEF
    private var tracksAdapter: TracksAdapter? = null

    private val viewModel: TracksViewModel by viewModel()

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce = debounce(
            delayMillis = CLICK_DEBOUNCE_DELAY,
            coroutineScope = viewLifecycleOwner.lifecycleScope,
            useLastParam = false,
            action = { track ->
                findNavController().navigate(
                    R.id.action_searchFragment_to_audioPlayerFragment,
                    AudioPlayerFragment.createArgs(track.toParcelable()
                    )
                )

                viewModel.onTrackClicked(track)
            }
        )

        tracksAdapter = TracksAdapter { track ->
            onTrackClickDebounce(track)
        }
        query = savedInstanceState?.getString(QUERY) ?: QUERY_DEF
        binding.etSearch.setText(query)

        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.recyclerView.adapter = tracksAdapter

        binding.ivClear.setOnClickListener {
            viewModel.cancelSearch()
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

        binding.etSearch.doOnTextChanged { s, _, _, _ ->
            viewModel.searchDebounce(s.toString())

            binding.ivClear.visibility = clearButtonVisibility(s)
            if (binding.ivClear.isGone) inputMethodManager?.hideSoftInputFromWindow(
                binding.etSearch.windowToken, 0
            )
            if (binding.etSearch.hasFocus() && s?.isEmpty() == true) {
                viewModel.onShowTracksHistory()
            } else hideTracksHistory()
        }

        binding.etSearch.doAfterTextChanged { s -> query = s.toString() }

        viewModel.observeTracksStateLiveData().observe(viewLifecycleOwner) {
            render(it)
        }

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
        tracksAdapter?.submitList(foundTracks.toList())
    }

    fun showEmptyState(message: String) {
        hideLoader()
        tracksAdapter?.submitList(listOf())

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
        tracksAdapter?.submitList(listOf())

        binding.apply {
            ivPlaceholder.setImageResource(R.drawable.ic_network_issues_120)
            tvServerResponse.text = message

            btnRenew.visibility = View.VISIBLE
            llPlaceholder.visibility = View.VISIBLE
        }
    }


    fun showTracksHistory(receivedTracks: List<Track>) {
        hideLoader()
        tracksAdapter?.submitList(receivedTracks)

        binding.apply {
            llPlaceholder.visibility = View.GONE
            tvYouSearched.visibility = View.VISIBLE
            btnClearHistory.visibility = View.VISIBLE
        }
    }

    fun hideTracksHistory() {
        tracksAdapter?.submitList(listOf())

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(QUERY, query)
    }

    companion object {
        private const val QUERY = "QUERY"
        private const val QUERY_DEF = ""
        private const val CLICK_DEBOUNCE_DELAY = 300L
    }
}

private fun clearButtonVisibility(s: CharSequence?): Int {
    return if (s.isNullOrEmpty()) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

