package com.practicum.playlistmaker.settings.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import com.practicum.playlistmaker.settings.ui.viewmodel.SettingsViewModelFactory

class SettingsActivity : AppCompatActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory(applicationContext)
    }

    private lateinit var themeSwitcher: SwitchMaterial
    private lateinit var shareButton: TextView
    private lateinit var supportButton: TextView
    private lateinit var agreementButton: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        initViews()
        setupListeners()
        setupObservers()
    }

    private fun initViews() {
        themeSwitcher = findViewById(R.id.switch_option)
        shareButton = findViewById(R.id.settings_item_2)
        supportButton = findViewById(R.id.settings_item_3)
        agreementButton = findViewById(R.id.settings_item_4)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupListeners() {
        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            settingsViewModel.switchTheme(checked)
        }

        shareButton.setOnClickListener {
            settingsViewModel.onShareButtonClicked()
        }

        supportButton.setOnClickListener {
            settingsViewModel.onSupportButtonClicked()
        }

        agreementButton.setOnClickListener {
            settingsViewModel.onAgreementButtonClicked()
        }
    }

    private fun setupObservers() {
        settingsViewModel.isDarkThemeEnabled.observe(this) { isEnabled ->
            themeSwitcher.isChecked = isEnabled
        }

        settingsViewModel.shareLink.observe(this) { link ->
            shareText(link)
        }

        settingsViewModel.supportEmailData.observe(this) { emailData ->
            sendEmail(emailData.email, emailData.subject, emailData.body)
        }

        settingsViewModel.userAgreementLink.observe(this) { link ->
            openUrl(link)
        }
    }

    private fun shareText(text: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        startActivity(shareIntent)
    }

    private fun sendEmail(email: String, subject: String, body: String) {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        startActivity(emailIntent)
    }

    private fun openUrl(url: String) {
        val userAgreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(userAgreementIntent)
    }
}
