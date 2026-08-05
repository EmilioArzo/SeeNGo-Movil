package com.emilionavarro.prueba.dispositivos.data.spotify

object SpotifyConfig {
    // TODO: pega aquí tu Client ID real (Spotify Developer Dashboard)
    const val CLIENT_ID = "TU_SPOTIFY_CLIENT_ID"

    const val REDIRECT_SCHEME = "seengo"
    const val REDIRECT_HOST   = "spotify-callback"
    const val REDIRECT_URI    = "$REDIRECT_SCHEME://$REDIRECT_HOST"

    // Ajusta según lo que vayas a controlar con las señas
    val SCOPES = listOf(
        "user-read-email",
        "user-read-playback-state",
        "user-modify-playback-state",
        "user-read-currently-playing",
        "playlist-read-private",
        "streaming"
    )
}