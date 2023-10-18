package com.practicum.playlistmaker.sharing.domain.impl

import com.practicum.playlistmaker.sharing.domain.api.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.models.EmailData
import com.practicum.playlistmaker.util.Constants
class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(Constants.APP_LINK)
    }
    override fun openTerms() {
        externalNavigator.openLink(Constants.TERM_LINK)

    }
    override fun openSupport() {
        externalNavigator.openEmail(EmailData(Constants.SUPPORT_EMAIL))
    }
}