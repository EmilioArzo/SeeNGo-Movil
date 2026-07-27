package com.emilionavarro.prueba.autenticacion.viewmodel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.emilionavarro.prueba.autenticacion.data.local.SessionManager
import com.emilionavarro.prueba.autenticacion.data.network.RetrofitClient
import com.emilionavarro.prueba.autenticacion.data.repository.AuthRepository


class LoginViewModelFactory(
    private val session: SessionManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(
                repository = AuthRepository(RetrofitClient.authApi),
                session    = session
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}