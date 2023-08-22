package com.practicum.playlistmaker

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import searchtrack.Track
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity: AppCompatActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        val track = intent.serializable<Track>(Constants.TRACK)

        ivBack.setOnClickListener {
            finish()
        }

        Glide.with(this)
            .load(track!!.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg"))
            .placeholder(R.drawable.plug)
            .centerInside()
            .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.frame_8dp)))
            .into(ivPictureArtist)

        tvNameTrack.text = track.trackName
        tvNameArtist.text = track.artistName
        tvCountdown.text = "0:36"
        tvDurationName.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        tvAlbumName.text =
            if (track.collectionName.isNullOrEmpty()) ({
                tvAlbumName.visibility = View.GONE
                tvAlbum.visibility = View.GONE
            }).toString() else track.collectionName

        tvYearName.text = track.releaseDate.subSequence(0, 4)
        tvGenreName.text = track.primaryGenreName
        tvCountryName.text = track.country

    }

    private inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
    }
}