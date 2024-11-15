package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

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
        val email = getString(R.string.support_email)
        val subject = Uri.encode(getString(R.string.email_subject))
        val body = Uri.encode(getString(R.string.email_body))

        val mailtoUri = Uri.parse("mailto:$email?subject=$subject&body=$body")

        // Явный интент для открытия почтового клиента
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = mailtoUri
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, getString(R.string.no_email_client), Toast.LENGTH_SHORT).show()
        }
    }


    private fun openUserAgreement() {
        val url = getString(R.string.user_agreement_url)
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }
}
