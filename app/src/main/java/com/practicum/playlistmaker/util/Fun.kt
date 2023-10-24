package com.practicum.playlistmaker.util

fun getCoverArtwork(artworkUrl100: String) = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")