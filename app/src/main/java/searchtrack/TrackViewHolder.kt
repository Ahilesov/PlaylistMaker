package searchtrack


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(parentView: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parentView.context).inflate(R.layout.track_item, parentView, false)
) {


    private val trackName: TextView = itemView.findViewById(R.id.tvName_track)
    private val artistName: TextView = itemView.findViewById(R.id.tvName_artist)
    private val trackTime: TextView = itemView.findViewById(R.id.tvTime_track)
    private val pictureArtist: ImageView = itemView.findViewById(R.id.ivPicture_artist)

    fun bind(item: Track) {
        trackName.text = item.trackName
        artistName.text = item.artistName
        // Время специальным классом для преобразования даты
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(item.trackTimeMillis)

        Glide.with(itemView)
            .load(item.artworkUrl100)
            .placeholder(R.drawable.plug)
            .centerCrop()
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.frame_2dp)))
            .into(pictureArtist)
    }
}