package com.practicum.playlistmaker


import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)


        val backButton = findViewById<ImageView>(R.id.back)
        backButton.setOnClickListener {
            finish()
        }

        val shareApp = findViewById<FrameLayout>(R.id.share_app)
        shareApp.setOnClickListener {
            val message = "https://practicum.yandex.ru/profile/android-developer/"
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(shareIntent)
        }

        val writeToSupport = findViewById<FrameLayout>(R.id.write_to_support)
        writeToSupport.setOnClickListener {
            val themeLetters = getString(R.string.thema_letters_to_support)
            val textLetters = getString(R.string.text_letters_to_support)
            val shareIntent = Intent(Intent.ACTION_SENDTO)
            shareIntent.data = Uri.parse("mailto:")
            shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("contextus.lab@yandex.ru"))
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, themeLetters)
            shareIntent.putExtra(Intent.EXTRA_TEXT, textLetters)
            startActivity(shareIntent)
        }

        val userAgreement = findViewById<FrameLayout>(R.id.user_agreement)
        userAgreement.setOnClickListener{
            val link = "https://yandex.ru/legal/practicum_offer/"
            val shareIntent = Intent(Intent.ACTION_VIEW)
            shareIntent.data = Uri.parse(link)
            startActivity(shareIntent)
        }


    }

}