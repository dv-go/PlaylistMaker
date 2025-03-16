package com.practicum.playlistmaker.search.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.ui.activity.MediaActivity
import com.practicum.playlistmaker.search.ui.presentation.SearchScreenState
import com.practicum.playlistmaker.search.ui.adapter.TracksAdapter
import com.practicum.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.practicum.playlistmaker.search.ui.viewmodel.SearchViewModelFactory

class SearchActivity : AppCompatActivity() {

    private var searchText: String = ""

    private val adapter = TracksAdapter { track ->
        viewModel.onTrackClicked(track)
    }

    private lateinit var inputEditText: EditText
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderImage: ImageView
    private lateinit var refreshButton: Button
    private lateinit var trackListView: RecyclerView
    private lateinit var toolbar: Toolbar
    private lateinit var clearHistoryButton: Button
    private lateinit var historyMessage: TextView
    private lateinit var progressBar: ProgressBar

    private val viewModel: SearchViewModel by viewModels {
        SearchViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        inputEditText = findViewById(R.id.searchEditText)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        placeholderImage = findViewById(R.id.placeholderImage)
        refreshButton = findViewById(R.id.refreshButton)
        refreshButton.visibility = View.GONE
        trackListView = findViewById(R.id.recyclerView)
        toolbar = findViewById(R.id.toolbar)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
        historyMessage = findViewById(R.id.yourHistoryMessage)
        progressBar = findViewById(R.id.progressBar)

        trackListView.adapter = adapter

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString("SEARCH_TEXT", "")
            inputEditText.setText(searchText)
        }

        setupListeners()
        setupObservers()

        viewModel.loadHistory()
    }

    private fun setupObservers() {

        viewModel.searchScreenState.observe(this) { state ->
            when (state) {
                is SearchScreenState.Loading -> showLoadingState()
                is SearchScreenState.Loaded -> showLoadedState(state)
                is SearchScreenState.Error -> showErrorState(state)
                is SearchScreenState.History -> showHistoryState(state)
                is SearchScreenState.EmptyHistory -> showEmptyHistoryState()
                is SearchScreenState.Typing -> showTypingState()
                is SearchScreenState.ShowToast -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.navigateToMediaActivity.observe(this) { track ->
            track?.let {
                val bundle = Bundle().apply {
                    putSerializable("TRACK", track)
                }
                val intent = Intent(this, MediaActivity::class.java).apply {
                    putExtras(bundle)
                }
                startActivity(intent)

            }
        }

    }

    private fun setupListeners() {

        toolbar.setNavigationOnClickListener {
            finish()
        }

        inputEditText.addTextChangedListener(object : TextWatcher {
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

            override fun afterTextChanged(s: Editable?) {}
        })

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchText.isEmpty()) {
                viewModel.loadHistory()
            } else if (!hasFocus) {
                viewModel.loadHistory()
            }
        }

        inputEditText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (inputEditText.right - inputEditText.compoundPaddingEnd)) {
                    inputEditText.setText("")
                    inputEditText.clearFocus()
                    setKeyboardVisibility(false)
                    adapter.setTracks(emptyList())
                    viewModel.cancelSearchDebounce()
                    viewModel.loadHistory()
                    true
                } else false
            } else false
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            }
            false
        }

        clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }

        clearHistoryButton.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (inputEditText.hasFocus()) {
                    inputEditText.clearFocus()
                } else {
                    inputEditText.requestFocusFromTouch()
                    inputEditText.clearFocus()
                }
                clearHistoryButton.performClick()
                true
            } else {
                false
            }
        }

        refreshButton.setOnClickListener {
            viewModel.performClickWithDebounce {
                search()
            }
        }

        refreshButton.setOnTouchListener{ _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (inputEditText.hasFocus()) {
                    inputEditText.clearFocus()
                } else {
                    inputEditText.requestFocusFromTouch()
                    inputEditText.clearFocus()
                }
                refreshButton.performClick()
                true
            } else {
                false
            }
        }

    }

    private fun search() {
        if (inputEditText.hasFocus()) {
            setKeyboardVisibility(false)
        }
        viewModel.search(inputEditText.text.toString())
    }

    private fun showLoadingState() {
        progressBar.visibility = View.VISIBLE
        trackListView.visibility = View.GONE
        placeholderMessage.visibility = View.GONE
        placeholderImage.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
        historyMessage.visibility = View.GONE
        refreshButton.visibility = View.GONE
        setClearButtonVisibility(R.drawable.ic_clear_button)
        setKeyboardVisibility(false)
    }

    private fun showLoadedState(state: SearchScreenState.Loaded) {
        progressBar.visibility = View.GONE
        trackListView.visibility = View.VISIBLE
        refreshButton.visibility = View.GONE
        adapter.setTracks(state.tracks)
        setClearButtonVisibility(R.drawable.ic_clear_button)
    }

    private fun showErrorState(state: SearchScreenState.Error) {
        progressBar.visibility = View.GONE
        trackListView.visibility = View.GONE
        historyMessage.visibility = View.GONE
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

    private fun showHistoryState(state: SearchScreenState.History) {
        progressBar.visibility = View.GONE
        trackListView.visibility = View.VISIBLE
        historyMessage.visibility = View.VISIBLE
        clearHistoryButton.visibility = View.VISIBLE
        placeholderMessage.visibility = View.GONE
        placeholderImage.visibility = View.GONE
        refreshButton.visibility = View.GONE
        adapter.setTracks(state.tracks)
        setClearButtonVisibility(null)
    }

    private fun showEmptyHistoryState() {
        progressBar.visibility = View.GONE
        trackListView.visibility = View.GONE
        historyMessage.visibility = View.GONE
        placeholderMessage.visibility = View.GONE
        placeholderImage.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
        refreshButton.visibility = View.GONE
        setClearButtonVisibility(null)
        setKeyboardVisibility(false)
    }

    private fun showTypingState() {
        progressBar.visibility = View.GONE
        trackListView.visibility = View.GONE
        historyMessage.visibility = View.GONE
        placeholderMessage.visibility = View.GONE
        placeholderImage.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
        refreshButton.visibility = View.GONE
        setClearButtonVisibility(R.drawable.ic_clear_button)
        setKeyboardVisibility(true)
    }

    private fun setClearButtonVisibility(iconRes: Int?) {
        val searchIcon = ContextCompat.getDrawable(this, R.drawable.ic_search_loup)
        val clearIcon = iconRes?.let { ContextCompat.getDrawable(this, it) }
        inputEditText.setCompoundDrawablesWithIntrinsicBounds(searchIcon, null, clearIcon, null)
    }

    private fun setKeyboardVisibility(shouldShow: Boolean) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (shouldShow) {
            imm.showSoftInput(inputEditText, InputMethodManager.SHOW_IMPLICIT)
        } else {
            imm.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("SEARCH_TEXT", searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString("SEARCH_TEXT", "")
        inputEditText.setText(searchText)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.cancelSearchDebounce()
    }
}
