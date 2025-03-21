package com.practicum.playlistmaker.main.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.ui.activity.SearchActivity
import com.practicum.playlistmaker.settings.ui.activity.SettingsActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button1ClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                val displayIntent = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(displayIntent)
            }
        }

        val button2ClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
//                val displayIntent = Intent(this@MainActivity, MediaActivity::class.java).apply {
//                    putExtra("IS_FROM_MAIN", true)
//                }
//                startActivity(displayIntent)
            }
        }

        val button1 = findViewById<Button>(R.id.button_1)
        button1.setOnClickListener(button1ClickListener)

        val button2 = findViewById<Button>(R.id.button_2)
        button2.setOnClickListener(button2ClickListener)

        val button3 = findViewById<Button>(R.id.button_3)
        button3.setOnClickListener {
            val displayIntent = Intent(this, SettingsActivity::class.java)
            startActivity(displayIntent)
        }
    }
}