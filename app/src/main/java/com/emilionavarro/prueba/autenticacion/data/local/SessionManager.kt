package com.emilionavarro.prueba.autenticacion.data.local



import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("seengo_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN     = "jwt_token"
        private const val KEY_USER_ID   = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL= "user_email"
        private const val KEY_USER_ROLE = "user_role"
    }

    fun saveSession(token: String, id: String, name: String, email: String, role: String) {
        prefs.edit()
            .putString(KEY_TOKEN,      token)
            .putString(KEY_USER_ID,    id)
            .putString(KEY_USER_NAME,  name)
            .putString(KEY_USER_EMAIL, email)
            .putString(KEY_USER_ROLE,  role)
            .apply()
    }

    fun getToken(): String?     = prefs.getString(KEY_TOKEN,      null)
    fun getUserId(): String?    = prefs.getString(KEY_USER_ID,    null)
    fun getUserName(): String?  = prefs.getString(KEY_USER_NAME,  null)
    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)
    fun getUserRole(): String?  = prefs.getString(KEY_USER_ROLE,  null)

    fun isLoggedIn(): Boolean   = getToken() != null

    fun clearSession() = prefs.edit().clear().apply()
}