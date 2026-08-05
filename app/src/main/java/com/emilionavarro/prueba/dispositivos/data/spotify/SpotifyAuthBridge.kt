package com.emilionavarro.prueba.dispositivos.data.spotify

import android.content.Intent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SpotifyAuthBridge {

    private val _authCode = MutableStateFlow<String?>(null)
    val authCode: StateFlow<String?> = _authCode.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    var pendingCodeVerifier: String? = null
        private set

    /** Llamado antes de abrir el navegador; genera y guarda el code_verifier de PKCE. */
    fun beginAuth(): String {
        val verifier = PkceGenerator.generateCodeVerifier()
        pendingCodeVerifier = verifier
        return verifier
    }

    /** Llamado desde MainActivity.onNewIntent / onCreate cuando vuelve el deep link. */
    fun handleIntent(intent: Intent?) {
        val uri = intent?.data ?: return
        if (uri.scheme == SpotifyConfig.REDIRECT_SCHEME && uri.host == SpotifyConfig.REDIRECT_HOST) {
            uri.getQueryParameter("code")?.let { _authCode.value = it }
            uri.getQueryParameter("error")?.let { _authError.value = it }
        }
    }

    fun consume() {
        _authCode.value = null
        _authError.value = null
        pendingCodeVerifier = null
    }
}