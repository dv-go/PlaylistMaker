package com.practicum.playlistmaker.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.di.Creator
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.models.Track

class SearchActivity : AppCompatActivity() {

    private var searchText: String = ""

    private val adapter = TracksAdapter { track ->
        performClickWithDebounce {
            tracksInteractor.saveToHistory(track)
            val intent = Intent(this, MediaActivity::class.java).apply {
                putExtra("TRACK", track)
            }
            startActivity(intent)
        }
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

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { search() }

    private var isClickAllowed = true

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

        setClearButtonVisibility(searchText.isNotEmpty())

        setupListeners()
        updateHistory()
    }

    private fun performClickWithDebounce(action: () -> Unit) {
        if (isClickAllowed) {
            isClickAllowed = false
            action()
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun setupListeners(){
        refreshButton.setOnClickListener{
            search()
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
            tracksInteractor.clearHistory()
            adapter.setTracks(emptyList())
            showHistoryUI(false)
            Toast.makeText(this, "История поиска очищена", Toast.LENGTH_SHORT).show()
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
                setClearButtonVisibility(searchText.isNotEmpty())

                if (searchText.isEmpty() && inputEditText.hasFocus()) {
                    showHistoryUI(true)
                    updateHistory()
                    hidePlaceholders()
                } else {
                    showHistoryUI(false)
                }

                if (searchText.isNotEmpty()) {
                    searchDebounce()
                } else {
                    handler.removeCallbacks(searchRunnable)
                }

            }

            override fun afterTextChanged(s: Editable?) {}
        })

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchText.isEmpty()) {
                showHistoryUI(true)
                updateHistory()
            } else if (!hasFocus) {
                showHistoryUI(false)
            }
        }

        inputEditText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (inputEditText.right - inputEditText.compoundPaddingEnd)) {
                    inputEditText.setText("")
                    inputEditText.clearFocus()
                    hideKeyboard()
                    setClearButtonVisibility(false)
                    adapter.setTracks(emptyList())
                    hidePlaceholders()
                    handler.removeCallbacks(searchRunnable)
                    showMessage("")
                    updateHistory()
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

    private fun hidePlaceholders(){
        placeholderMessage.visibility = View.GONE
        placeholderImage.visibility = View.GONE
        refreshButton.visibility = View.GONE
    }

    private fun showHistoryUI(show: Boolean) {
        val visibility = if (show) View.VISIBLE else View.GONE
        historyMessage.visibility = visibility
        clearHistoryButton.visibility = visibility
        trackListView.visibility = visibility
    }

    private fun updateHistory() {
        val historyTracks = tracksInteractor.getSearchHistory()
        if (historyTracks.isEmpty()) {
            adapter.setTracks(emptyList())
            showHistoryUI(false)
            hidePlaceholders()
        } else {
            adapter.setTracks(historyTracks)
            showHistoryUI(true)
        }
    }

    private fun setClearButtonVisibility(visible: Boolean) {
        val searchIcon = ContextCompat.getDrawable(this, R.drawable.ic_search_loup)
        val clearIcon =
            if (visible) ContextCompat.getDrawable(this, R.drawable.ic_clear_button) else null
        inputEditText.setCompoundDrawablesWithIntrinsicBounds(searchIcon, null, clearIcon, null)
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

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus
        view?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun showMessage(text: String) {
        when (text) {
            getString(R.string.nothing_found) -> updateMessageUI(true, text, R.drawable.nothing_found)
            getString(R.string.something_went_wrong) -> updateMessageUI(true, text, R.drawable.no_connection)
            else -> updateMessageUI(false)
        }
        adapter.setTracks(emptyList())
    }

    private fun updateMessageUI(isVisible: Boolean, message: String? = null, imageRes: Int? = null) {
        placeholderMessage.visibility = if (isVisible) View.VISIBLE else View.GONE
        placeholderImage.visibility = if (isVisible) View.VISIBLE else View.GONE
        refreshButton.visibility = if (isVisible && imageRes == R.drawable.no_connection) View.VISIBLE else View.GONE

        message?.let { placeholderMessage.text = it }
        imageRes?.let { placeholderImage.setImageResource(it) }
    }

    private fun search() {
        if (inputEditText.hasFocus()) {
            hideKeyboard()
        }

        hidePlaceholders()
        trackListView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE

        tracksInteractor.searchTracks(searchText, object : TracksInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>) {
                runOnUiThread {
                    progressBar.visibility = View.GONE
                    handleSearchResult(foundTracks)
                }
            }
        })
    }

    private fun handleSearchResult(foundTracks: List<Track>) {
        if (foundTracks.isNotEmpty()) {
            adapter.setTracks(foundTracks)
            trackListView.visibility = View.VISIBLE
        } else {
            showMessage(getString(R.string.nothing_found))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(searchRunnable)
    }

}


