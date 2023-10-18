package com.practicum.playlistmaker.sharing.data.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.domain.api.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.models.EmailData

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {

    private val themeLetters = context.getString(R.string.thema_letters_to_support)
    private val textLetters = context.getString(R.string.text_letters_to_support)
    override fun shareLink(shareText: String) {
        Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
            context.startActivity(
                Intent.createChooser(this, null).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }
    override fun openLink(termsLink: String) {
        val browserIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse(termsLink)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(browserIntent)
    }
    override fun openEmail(supportEmailData: EmailData) {
        Intent().apply {
            action = Intent.ACTION_SENDTO
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmailData.mail))
            putExtra(Intent.EXTRA_SUBJECT, themeLetters)
            putExtra(Intent.EXTRA_TEXT, textLetters)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(this)
        }
    }
}