package com.practicum.playlistmaker.presentation

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.interactor.SettingsInteractorImpl
import com.practicum.playlistmaker.data.repository.ThemeRepositoryImpl
import com.practicum.playlistmaker.domain.api.SettingsInteractor

class SettingsActivity : AppCompatActivity() {

    private lateinit var settingsInteractor: SettingsInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val themeRepository = ThemeRepositoryImpl(applicationContext)
        settingsInteractor = SettingsInteractorImpl(applicationContext, themeRepository)

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.switch_option)
        themeSwitcher.isChecked = settingsInteractor.isDarkThemeEnabled()
        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            settingsInteractor.switchTheme(checked)
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val shareButton = findViewById<TextView>(R.id.settings_item_2)
        shareButton.setOnClickListener {
            startActivity(settingsInteractor.getShareIntent())
        }

        val supportButton = findViewById<TextView>(R.id.settings_item_3)
        supportButton.setOnClickListener {
            startActivity(settingsInteractor.getSupportEmailIntent())
        }

        val agreementButton = findViewById<TextView>(R.id.settings_item_4)
        agreementButton.setOnClickListener {
            startActivity(settingsInteractor.getUserAgreementIntent())
        }
    }
}
