package com.practicum.playlistmaker.domain.interactor

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.SettingsInteractor
import com.practicum.playlistmaker.domain.api.ThemeRepository

class SettingsInteractorImpl(
    private val context: Context,
    private val themeRepository: ThemeRepository
) : SettingsInteractor {

    override fun isDarkThemeEnabled(): Boolean {
        return themeRepository.isDarkThemeEnabled()
    }

    override fun switchTheme(isEnabled: Boolean) {
        themeRepository.switchTheme(isEnabled)
    }

    override fun getShareIntent(): Intent {
        val link = context.getString(R.string.user_agreement_url)
        return Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, link)
            type = "text/plain"
        }
    }

    override fun getSupportEmailIntent(): Intent {
        return Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.support_email)))
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.email_subject))
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.email_body))
        }
    }

    override fun getUserAgreementIntent(): Intent {
        val url = context.getString(R.string.user_agreement_url)
        return Intent(Intent.ACTION_VIEW, Uri.parse(url))
    }
}
