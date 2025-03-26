package com.practicum.playlistmaker.settings.data.repository

import android.content.Context
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.settings.data.dto.SupportEmailData
import com.practicum.playlistmaker.settings.domain.api.ExternalNavigator

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {

    override fun getShareLink(): String {
        return context.getString(R.string.user_agreement_url)
    }

    override fun getSupportEmailData(): SupportEmailData {
        return SupportEmailData(
            email = context.getString(R.string.support_email),
            subject = context.getString(R.string.email_subject),
            body = context.getString(R.string.email_body)
        )
    }

    override fun getUserAgreementLink(): String {
        return context.getString(R.string.user_agreement_url)
    }
}
