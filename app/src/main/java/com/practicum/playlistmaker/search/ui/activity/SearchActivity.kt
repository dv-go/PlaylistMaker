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
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.player.ui.activity.MediaActivity
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
    private lateinit var toolbar:Toolbar
    private lateinit var clearHistoryButton: Button
    private lateinit var historyMessage: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var tracksInteractor: TracksInteractor

    private val viewModel: SearchViewModel by viewModels {
        SearchViewModelFactory(application, Creator.provideTracksInteractor())
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
        tracksInteractor = Creator.provideTracksInteractor()

        trackListView.adapter = adapter

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString("SEARCH_TEXT", "")
            inputEditText.setText(searchText)
        }

        viewModel.updateClearButtonVisibility(searchText.isNotEmpty())

        setupListeners()
        setupObservers()

        viewModel.loadHistory()
    }

    private fun setupObservers() {

        viewModel.tracks.observe(this) { tracks ->
            adapter.setTracks(tracks)
        }

        viewModel.trackListVisible.observe(this) { isVisible ->
            trackListView.visibility = if (isVisible) View.VISIBLE else View.GONE
            if (isVisible) {
                adapter.setTracks(viewModel.tracks.value ?: emptyList())
            }
        }

        viewModel.historyVisible.observe(this) { isVisible ->
            historyMessage.visibility = if (isVisible) View.VISIBLE else View.GONE
            clearHistoryButton.visibility = if (isVisible) View.VISIBLE else View.GONE
            if (isVisible) {
                adapter.setTracks(viewModel.tracks.value ?: emptyList())
                trackListView.visibility = View.VISIBLE
            }
        }

        viewModel.placeholderState.observe(this) { state ->
            placeholderMessage.apply {
                text = state.message
                visibility = if (state.isVisible) View.VISIBLE else View.GONE
            }
            placeholderImage.apply {
                visibility = if (state.imageRes != null) View.VISIBLE else View.GONE
                state.imageRes?.let { setImageResource(it) }
            }
            refreshButton.visibility = if (state.isRefreshButtonVisible) View.VISIBLE else View.GONE
        }

        viewModel.navigateToMediaActivity.observe(this) { track ->
            track?.let {
                val intent = Intent(this, MediaActivity::class.java).apply {
                    putExtra("TRACK", it)
                }
                startActivity(intent)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.hideKeyboardEvent.observe(this) {
            hideKeyboard()
        }

        viewModel.showToastEvent.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.clearButtonState.observe(this) { ( _ , iconRes) ->
            setClearButtonVisibility(iconRes)
        }

    }

    private fun setupListeners(){
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

        toolbar.setNavigationOnClickListener {
            finish()
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

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s.toString()
                viewModel.onSearchTextChanged(searchText)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchText.isEmpty()) {
                viewModel.updateHistoryVisibility(true)
                viewModel.loadHistory()
            } else if (!hasFocus) {
                viewModel.updateHistoryVisibility(false)
            }
        }

        inputEditText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (inputEditText.right - inputEditText.compoundPaddingEnd)) {
                    inputEditText.setText("")
                    inputEditText.clearFocus()
                    hideKeyboard()
                    viewModel.updateClearButtonVisibility(false)
                    adapter.setTracks(emptyList())
                    viewModel.clearSearchResults()
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

    }

    private fun search() {
        if (inputEditText.hasFocus()) {
            hideKeyboard()
        }
        viewModel.search(inputEditText.text.toString())
    }

    private fun setClearButtonVisibility(iconRes: Int?) {
        val searchIcon = ContextCompat.getDrawable(this, R.drawable.ic_search_loup)
        val clearIcon = iconRes?.let { ContextCompat.getDrawable(this, it) }
        inputEditText.setCompoundDrawablesWithIntrinsicBounds(searchIcon, null, clearIcon, null)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus
        view?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
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


