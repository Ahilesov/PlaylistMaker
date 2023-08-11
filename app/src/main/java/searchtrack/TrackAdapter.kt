package searchtrack

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R

class TrackAdapter(val clickListener: LocationClickListener?) :
    RecyclerView.Adapter<TrackViewHolder>() {

    var listTrack: List<Track> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {

        return TrackViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return listTrack.size
    }


    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(listTrack[position])
        holder.itemView.setOnClickListener { clickListener?.onLocationClick(listTrack[position])}
    }

    fun interface LocationClickListener {
        fun onLocationClick(track: Track)
    }
}