package com.practicum.playlistmaker.domain.api

import android.content.Intent

interface SettingsInteractor {
    fun isDarkThemeEnabled(): Boolean
    fun switchTheme(isEnabled: Boolean)
    fun getShareIntent(): Intent
    fun getSupportEmailIntent(): Intent
    fun getUserAgreementIntent(): Intent
}
