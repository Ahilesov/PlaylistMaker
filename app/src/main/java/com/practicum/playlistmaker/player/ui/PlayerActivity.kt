package com.practicum.playlistmaker.player.ui

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.Constants
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.creator.Creator
import com.practicum.playlistmaker.player.domain.models.PlayerState
import searchtrack.Track
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private val ivBack: ImageView by lazy { findViewById(R.id.ivBack) }
    private val ivPictureArtist: ImageView by lazy { findViewById(R.id.ivPictureArtist) }
    private val tvNameTrack: TextView by lazy { findViewById(R.id.tvNameTrack) }
    private val tvNameArtist: TextView by lazy { findViewById(R.id.tvNameArtist) }
    private val tvCountdown: TextView by lazy { findViewById(R.id.tvCountdown) }
    private val tvDurationName: TextView by lazy { findViewById(R.id.tvDurationName) }
    private val tvAlbumName: TextView by lazy { findViewById(R.id.tvAlbumName) }
    private val tvAlbum: TextView by lazy { findViewById(R.id.tvAlbum) }
    private val tvYearName: TextView by lazy { findViewById(R.id.tvYearName) }
    private val tvGenreName: TextView by lazy { findViewById(R.id.tvGenreName) }
    private val tvCountryName: TextView by lazy { findViewById(R.id.tvCountryName) }
    private val ivPlayTrack: ImageView by lazy { findViewById(R.id.ivPlayTrack) }

    private val getPlayerInteractorImpl = Creator.provideGetPlayerInteractorImpl()

    private lateinit var handler: Handler
    private val runnable = object : Runnable {
        override fun run() {
            tvCountdown.text =
                SimpleDateFormat("mm:ss", Locale.getDefault())
                    .format(getPlayerInteractorImpl.getCurrentPosition())
            handler.postDelayed(this, Constants.TRACK_TIMER_DEBOUNCE_DELAY)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        val track = intent.serializable<Track>(Constants.TRACK)

        ivBack.setOnClickListener {
            finish()
        }

        Glide.with(this)
            .load(track!!.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.plug)
            .centerInside()
            .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.frame_8dp)))
            .into(ivPictureArtist)

        tvNameTrack.text = track.trackName
        tvNameArtist.text = track.artistName

        tvDurationName.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        tvAlbumName.text =
            if (track.collectionName.isNullOrEmpty()) ({
                tvAlbumName.visibility = View.GONE
                tvAlbum.visibility = View.GONE
            }).toString() else track.collectionName

        if (track.releaseDate.isNullOrEmpty()) {
            tvYearName.text = ""
        } else tvYearName.text = track.releaseDate.substring(0, 4)

        tvGenreName.text = track.primaryGenreName
        tvCountryName.text = track.country

        handler = Handler(Looper.getMainLooper())
        preparePlayer(track.previewUrl)

        ivPlayTrack.setOnClickListener {
            playbackControl()
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        getPlayerInteractorImpl.releasePlayer()
        handler.removeCallbacks(runnable)
    }

    private inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(
            key,
            T::class.java
        )

        else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
    }

    private fun preparePlayer(url: String) {
        getPlayerInteractorImpl.preparePlayer(url)
        getSetOnCompletionListener()
    }

    private fun getSetOnCompletionListener() {
        getPlayerInteractorImpl.setTrackCompletionListener {
            ivPlayTrack.setImageResource(R.drawable.play_button)
            handler.removeCallbacks(runnable)
            tvCountdown.text = Constants.TRACK_COUNTDOWN_TEXT
        }
    }

    private fun startPlayer() {
        getPlayerInteractorImpl.startPlayer()
        ivPlayTrack.setImageResource(R.drawable.pause_button)
        handler.post(runnable)
    }

    private fun pausePlayer() {
        getPlayerInteractorImpl.pausePlayer()
        ivPlayTrack.setImageResource(R.drawable.play_button)
        handler.removeCallbacks(runnable)
    }

    private fun playbackControl() {
        when (getPlayerInteractorImpl.getPlayerState()) {
            PlayerState.PLAYING -> {
                pausePlayer()
            }

            PlayerState.PAUSED, PlayerState.PREPARED -> {
                startPlayer()
            }
            PlayerState.DEFAULT -> {
                startPlayer()
                getSetOnCompletionListener()
            }
        }
    }
}