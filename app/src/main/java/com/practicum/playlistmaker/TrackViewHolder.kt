package com.practicum.playlistmaker


import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackName: TextView = itemView.findViewById(R.id.tvName_track)
    private val artistName: TextView = itemView.findViewById(R.id.tvName_artist)
    private val trackTime: TextView = itemView.findViewById(R.id.tvTime_track)
    private val pictureArtist: ImageView = itemView.findViewById(R.id.ivPicture_artist)

    fun bind(item: Track) {
        trackName.text = item.trackName
        artistName.text = item.artistName
        trackTime.text = item.trackTime
        Glide.with(itemView)
            .load(item.artworkUrl100)
            .placeholder(R.drawable.plug)
            .centerCrop()
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.frame_2dp)))
            .into(pictureArtist)
    }
}