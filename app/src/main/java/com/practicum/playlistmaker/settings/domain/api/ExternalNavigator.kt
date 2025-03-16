package com.practicum.playlistmaker.settings.domain.api

import com.practicum.playlistmaker.settings.data.dto.SupportEmailData

interface ExternalNavigator {
    fun getShareLink(): String
    fun getSupportEmailData(): SupportEmailData
    fun getUserAgreementLink(): String
}