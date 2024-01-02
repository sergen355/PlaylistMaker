package com.practicum.playlistmaker.domain.sharing.impl

import com.practicum.playlistmaker.data.model.EmailData
import com.practicum.playlistmaker.data.sharing.ExternalNavigator
import com.practicum.playlistmaker.domain.sharing.SharingInteractor

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        return "http://practicum.yandex.ru"
    }

    private fun getSupportEmailData(): EmailData {
        val message: String = "Спасибо разработчикам и разработчицам за крутое приложение!"
        val subject: String = "Сообщение разработчикам и разработчицам приложения Playlist Maker"
        val fromAddress: String = "xxxxxvic@yandex.ru"
        return EmailData(message, subject, fromAddress)
    }

    private fun getTermsLink(): String {
        return "https://yandex.ru/legal/practicum_offer/"
    }
}