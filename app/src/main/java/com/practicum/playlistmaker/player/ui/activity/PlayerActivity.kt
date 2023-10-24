package com.practicum.playlistmaker.player.ui.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.util.Constants
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.util.getCoverArtwork
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding
    private lateinit var viewModel: PlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val track = intent.serializable<Track>(Constants.TRACK)

        viewModel = ViewModelProvider(this, PlayerActivityViewModelFactory(track!!))
            .get(PlayerViewModel::class.java)

        binding.ivBack.setOnClickListener {
            finish()
        }

        viewModel.playCountdown.observe(this) { duration ->
            binding.tvCountdown.text = duration
        }

        Glide.with(this)
            .load(getCoverArtwork(track!!.artworkUrl100))
            .placeholder(R.drawable.plug)
            .centerInside()
            .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.frame_8dp)))
            .into(binding.ivPictureArtist)

        binding.tvNameTrack.text = track.trackName
        binding.tvNameArtist.text = track.artistName

        binding.tvDurationName.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        binding.tvAlbumName.text =
            if (track.collectionName.isNullOrEmpty()) ({
                binding.tvAlbumName.visibility = View.GONE
                binding.tvAlbum.visibility = View.GONE
            }).toString() else track.collectionName

        if (track.releaseDate.isNullOrEmpty()) {
            binding.tvYearName.text = ""
        } else binding.tvYearName.text = track.releaseDate.substring(0, 4)

        binding.tvGenreName.text = track.primaryGenreName
        binding.tvCountryName.text = track.country

        binding.ivPlayTrack.setOnClickListener {
            viewModel.playbackControl(track.previewUrl)
        }

        viewModel.playState.observe(this) { playState ->
            if (playState) {
                binding.ivPlayTrack.setImageResource(R.drawable.pause_button)
            } else {
                binding.ivPlayTrack.setImageResource(R.drawable.play_button)
            }
        }
        viewModel.playCountdown.observe(this) { duration ->
            binding.tvCountdown.text = duration
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    private inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(
            key,
            T::class.java
        )

        else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
    }

}
