package com.practicum.playlistmaker.settings.ui.presentation

import com.practicum.playlistmaker.settings.data.dto.SupportEmailData

sealed class SettingsCommand {
    data class Share(val link: String) : SettingsCommand()
    data class SendEmail(val emailData: SupportEmailData) : SettingsCommand()
    data class OpenUrl(val url: String) : SettingsCommand()
}
