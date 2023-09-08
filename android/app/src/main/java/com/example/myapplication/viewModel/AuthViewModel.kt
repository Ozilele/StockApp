package com.example.myapplication.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.backend.auth.AuthRepository
import com.example.myapplication.data.backend.auth.AuthResult
import kotlinx.coroutines.launch

class AuthViewModel (
  private val repository: AuthRepository
) : ViewModel() {

  private var _authState = MutableLiveData(AuthState(isLoading = true, authResponse = AuthResult.Unauthorized())) // state of the auth mechanism
  val authState: LiveData<AuthState> get() = _authState

  init {
    authenticate()
  }

  fun register(name: String, email: String, password: String) {
    viewModelScope.launch {
      _authState.value = _authState.value?.copy(isLoading = true)
      val result = repository.signUp(name, email, password)
      _authState.value = _authState.value?.copy(authResponse = result)
      _authState.value = _authState.value?.copy(isLoading = false)
    }
  }

  fun login(email: String, password: String) {
    viewModelScope.launch {
      _authState.value = _authState.value?.copy(isLoading = true)
      val result = repository.signIn(email, password)
      _authState.value = _authState.value?.copy(authResponse = result)
      _authState.value = _authState.value?.copy(isLoading = false)
    }
  }

  private fun authenticate() {
    viewModelScope.launch {
      _authState.value = _authState.value?.copy(isLoading = true)
      val result = repository.authenticate()
      println("$result")
      _authState.value = _authState.value?.copy(authResponse = result)
      _authState.value = _authState.value?.copy(isLoading = false)
    }
  }

  data class AuthState(
    var isLoading: Boolean,
    var authResponse: AuthResult<Unit>
  )

}