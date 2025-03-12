package com.practicum.playlistmaker.settings.ui.activity

import com.practicum.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import com.practicum.playlistmaker.settings.ui.viewmodel.SettingsViewModelFactory

import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator

class SettingsActivity : AppCompatActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory(Creator.provideSettingsInteractor())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.switch_option)

        settingsViewModel.isDarkThemeEnabled.observe(this) { isEnabled ->
            themeSwitcher.isChecked = isEnabled
        }

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            settingsViewModel.switchTheme(checked)
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val shareButton = findViewById<TextView>(R.id.settings_item_2)
        shareButton.setOnClickListener {
            startActivity(settingsViewModel.getShareIntent())
        }

        val supportButton = findViewById<TextView>(R.id.settings_item_3)
        supportButton.setOnClickListener {
            startActivity(settingsViewModel.getSupportEmailIntent())
        }

        val agreementButton = findViewById<TextView>(R.id.settings_item_4)
        agreementButton.setOnClickListener {
            startActivity(settingsViewModel.getUserAgreementIntent())
        }
    }
}
