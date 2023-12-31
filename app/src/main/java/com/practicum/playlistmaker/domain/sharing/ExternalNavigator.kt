package com.practicum.playlistmaker.domain.sharing

import com.practicum.playlistmaker.data.model.EmailData

interface ExternalNavigator {
    fun shareLink(sharedLink: String)
    fun openLink(termAddress: String)
    fun openEmail(dataToSend: EmailData)
}