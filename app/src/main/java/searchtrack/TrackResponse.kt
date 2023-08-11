package searchtrack

import searchtrack.Track

class TrackResponse (
    val resultCount: Int,
    val results: ArrayList<Track>
)