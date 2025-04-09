package com.practicum.playlistmaker.search.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.player.ui.activity.PlayerActivity
import com.practicum.playlistmaker.search.ui.adapter.TracksAdapter
import com.practicum.playlistmaker.search.ui.presentation.SearchScreenState
import com.practicum.playlistmaker.search.ui.viewmodel.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModel()
    private val adapter = TracksAdapter { track ->
        viewModel.onTrackClicked(track)
    }

    private var searchText = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView.adapter = adapter

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")
            binding.searchEditText.setText(searchText)
        }

        setupListeners()
        setupObservers()
        viewModel.loadHistory()
    }

    private fun setupListeners() = with(binding) {
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchText.isEmpty()) {
                viewModel.loadHistory()
            } else if (!hasFocus) {
                viewModel.loadHistory()
            }
        }

        searchEditText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP &&
                event.rawX >= (searchEditText.right - searchEditText.compoundPaddingEnd)
            ) {
                searchEditText.setText("")
                searchEditText.clearFocus()
                setKeyboardVisibility(false)
                adapter.setTracks(emptyList())
                viewModel.cancelSearchDebounce()
                viewModel.loadHistory()
                return@setOnTouchListener true
            }
            false
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            } else false
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s.toString()
                if (searchText.isNotBlank()) {
                    viewModel.onSearchTextChanged(searchText)
                } else {
                    viewModel.cancelSearchDebounce()
                    viewModel.loadHistory()
                }
            }
        })

        clearHistoryButton.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (binding.searchEditText.hasFocus()) {
                    binding.searchEditText.clearFocus()
                    setKeyboardVisibility(false)
                } else {
                    binding.searchEditText.requestFocusFromTouch()
                    binding.searchEditText.clearFocus()
                    setKeyboardVisibility(false)
                }

                binding.clearHistoryButton.performClick()
                return@setOnTouchListener true
            }
            false
        }

        clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }

        refreshButton.setOnClickListener {
            viewModel.performClickWithDebounce {
                search()
            }
        }
    }

    private fun setupObservers() = with(binding) {
        viewModel.searchScreenState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SearchScreenState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    placeholderMessage.visibility = View.GONE
                    placeholderImage.visibility = View.GONE
                    clearHistoryButton.visibility = View.GONE
                    yourHistoryMessage.visibility = View.GONE
                    refreshButton.visibility = View.GONE
                    setClearButtonVisibility(R.drawable.ic_clear_button)
                }

                is SearchScreenState.Loaded -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    refreshButton.visibility = View.GONE
                    adapter.setTracks(state.tracks)
                    setClearButtonVisibility(R.drawable.ic_clear_button)
                }

                is SearchScreenState.Error -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    yourHistoryMessage.visibility = View.GONE
                    placeholderMessage.visibility = View.VISIBLE
                    placeholderImage.visibility = View.VISIBLE
                    when (state.errorType) {
                        is SearchScreenState.ErrorType.NoNetwork -> {
                            placeholderMessage.text = getString(R.string.something_went_wrong)
                            placeholderImage.setImageResource(R.drawable.no_connection)
                            refreshButton.visibility = View.VISIBLE
                        }
                        is SearchScreenState.ErrorType.NoResults -> {
                            placeholderMessage.text = getString(R.string.nothing_found)
                            placeholderImage.setImageResource(R.drawable.nothing_found)
                            refreshButton.visibility = View.GONE
                        }
                    }
                    setClearButtonVisibility(null)
                }

                is SearchScreenState.History -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    yourHistoryMessage.visibility = View.VISIBLE
                    clearHistoryButton.visibility = View.VISIBLE
                    placeholderMessage.visibility = View.GONE
                    placeholderImage.visibility = View.GONE
                    refreshButton.visibility = View.GONE
                    adapter.setTracks(state.tracks)
                    setClearButtonVisibility(null)
                }

                is SearchScreenState.EmptyHistory -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    yourHistoryMessage.visibility = View.GONE
                    placeholderMessage.visibility = View.GONE
                    placeholderImage.visibility = View.GONE
                    clearHistoryButton.visibility = View.GONE
                    refreshButton.visibility = View.GONE
                    setClearButtonVisibility(null)
                }

                is SearchScreenState.Typing -> {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    yourHistoryMessage.visibility = View.GONE
                    placeholderMessage.visibility = View.GONE
                    placeholderImage.visibility = View.GONE
                    clearHistoryButton.visibility = View.GONE
                    refreshButton.visibility = View.GONE
                    setClearButtonVisibility(R.drawable.ic_clear_button)
                }

                is SearchScreenState.ShowToast -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.navigateToMediaActivity.observe(viewLifecycleOwner) { track ->
            track?.let {
                val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
                    putExtra(TRACK_KEY, track)
                }
                startActivity(intent)
                viewModel.onNavigatedToPlayer()
            }
        }
    }

    private fun search() {
        if (binding.searchEditText.hasFocus()) {
            setKeyboardVisibility(false)
        }
        viewModel.search(binding.searchEditText.text.toString())
    }


    private fun setClearButtonVisibility(iconRes: Int?) {
        val searchIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_search_loup)
        val clearIcon = iconRes?.let { ContextCompat.getDrawable(requireContext(), it) }
        binding.searchEditText.setCompoundDrawablesWithIntrinsicBounds(searchIcon, null, clearIcon, null)
    }

    private fun setKeyboardVisibility(shouldShow: Boolean) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (shouldShow) {
            imm.showSoftInput(binding.searchEditText, InputMethodManager.SHOW_IMPLICIT)
        } else {
            imm.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(SEARCH_TEXT_KEY, searchText)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val SEARCH_TEXT_KEY = "SEARCH_TEXT"
        private const val TRACK_KEY = "TRACK"
    }

}
