package com.practicum.playlistmaker

import android.content.Context
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
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Locale

class SearchActivity : AppCompatActivity() {

    private val APIBaseULR: String = "https://itunes.apple.com"
    private var searchText: String = ""

    private val retrofit = Retrofit.Builder()
        .baseUrl(APIBaseULR)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val trackServise = retrofit.create(ITunesSearchAPI::class.java)

    private val trackList = ArrayList<Track>()
    private val adapter = TracksAdapter()

    private lateinit var inputEditText: EditText
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderImage: ImageView
    private lateinit var refreshButton: Button
    private lateinit var trackListView: RecyclerView
    private lateinit var toolbar:Toolbar

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
        toolbar.setNavigationOnClickListener {
            finish()
        }

        adapter.tracksList = trackList
        trackListView.adapter = adapter

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString("SEARCH_TEXT", "")
            inputEditText.setText(searchText)
        }

        setClearButtonVisibility(searchText.isNotEmpty())

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s.toString()
                setClearButtonVisibility(searchText.isNotEmpty())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        inputEditText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (inputEditText.right - inputEditText.compoundPaddingEnd)) {
                    inputEditText.setText("")
                    hideKeyboard()
                    setClearButtonVisibility(false)
                    trackList.clear()
                    placeholderMessage.visibility = View.GONE
                    placeholderImage.visibility = View.GONE
                    refreshButton.visibility = View.GONE
                    adapter.notifyDataSetChanged()
                    inputEditText.performClick()
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

        refreshButton.setOnClickListener{
            search()
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
        if (text.isNotEmpty()) {
            placeholderMessage.visibility = View.VISIBLE
            placeholderImage.visibility = View.VISIBLE
            placeholderMessage.text = text
            when (text) {
                getString(R.string.nothing_found) -> placeholderImage.setImageResource(R.drawable.nothing_found)
                getString(R.string.something_went_wrong) -> {
                    placeholderImage.setImageResource(R.drawable.no_connection)
                    refreshButton.visibility = View.VISIBLE
                }
            }
            trackList.clear()
            adapter.notifyDataSetChanged()
        } else {
            placeholderMessage.visibility = View.GONE
            placeholderImage.visibility = View.GONE
            refreshButton.visibility = View.GONE
        }
    }


    fun formatTrackDuration(durationMs: String): String {
        return try {
            val durationLong = durationMs.toLong() // Преобразуем строку в Long
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(durationLong)
        } catch (e: NumberFormatException) {
            "00:00" // Возвращаем значение по умолчанию в случае ошибки
        }
    }


    private fun search(){
        placeholderMessage.visibility = View.GONE
        placeholderImage.visibility = View.GONE
        refreshButton.visibility = View.GONE

        trackServise.search(searchText)
            .enqueue(object : Callback<TrackResponse>{
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                ) {
                    when (response.code()){
                        200 -> {
                            if(response.body()?.results?.isNotEmpty() == true){
                                trackList.clear()
                                response.body()?.results?.forEach { track ->
                                    track.trackTimeMillis = formatTrackDuration(track.trackTimeMillis)
                                }
                                trackList.addAll(response.body()?.results!!)
                                adapter.notifyDataSetChanged()
                            } else {
                                showMessage(getString(R.string.nothing_found))
                            }
                        }
                        else -> showMessage(getString(R.string.something_went_wrong))
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    showMessage(getString(R.string.something_went_wrong))
                }
            })
    }

}


