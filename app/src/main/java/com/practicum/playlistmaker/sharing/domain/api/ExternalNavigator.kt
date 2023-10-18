package com.practicum.playlistmaker.sharing.domain.api

import com.practicum.playlistmaker.sharing.domain.models.EmailData

interface ExternalNavigator {
    fun shareLink(shareText:String)
    fun openLink(termsLink:String)
    fun openEmail(supportEmailData: EmailData)
}