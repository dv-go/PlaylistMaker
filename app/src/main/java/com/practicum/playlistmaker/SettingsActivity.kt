package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val app = applicationContext as App
        val isDarkThemeEnabled = app.darkTheme

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.switch_option)
        themeSwitcher.isChecked = isDarkThemeEnabled
        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            app.switchTheme(checked)
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val shareButton = findViewById<TextView>(R.id.settings_item_2)
        shareButton.setOnClickListener {
            shareLink()
        }

        val supportButton = findViewById<TextView>(R.id.settings_item_3)
        supportButton.setOnClickListener {
            openEmailClient()
        }

        val agreementButton = findViewById<TextView>(R.id.settings_item_4)
        agreementButton.setOnClickListener {
            openUserAgreement()
        }
    }

    private fun shareLink() {
        val link = getString(R.string.user_agreement_url)
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, link)
            type = "text/plain"
        }
        val chooserIntent = Intent.createChooser(shareIntent, getString(R.string.share_button_text))
        startActivity(chooserIntent)
    }

    private fun openEmailClient() {
        val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.email_body))
        }
        startActivity(supportIntent)
    }


    private fun openUserAgreement() {
        val url = getString(R.string.user_agreement_url)
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }
}
