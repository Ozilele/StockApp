package com.example.myapplication.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.myapplication.data.backend.auth.AuthResult
import com.example.myapplication.databinding.ActivityLoginBinding
import com.example.myapplication.viewModel.AuthViewModel
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

  private var isFirstRender = true
  private lateinit var binding: ActivityLoginBinding
  private lateinit var emailEditText: TextInputEditText
  private lateinit var passwordEditText: TextInputEditText
  private lateinit var loginButton: AppCompatButton
  private lateinit var forgetPasswordTextView: TextView
  private lateinit var registerTextView: TextView
  private lateinit var progressBar: ProgressBar
  @Inject
  lateinit var authViewModel: AuthViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityLoginBinding.inflate(layoutInflater)
    setContentView(binding.root)
    binding.apply {
      emailEditText = emailInputEditText
      passwordEditText = passwordInputEditText
      this@LoginActivity.loginButton = loginButton
      forgetPasswordTextView = forgetPasswordText
      registerTextView = registerNowText
      this@LoginActivity.progressBar = progressBar
    }

    observeViewModel()
    applyListeners()
  }


  private fun observeViewModel() {
    authViewModel.authState.observe(this) { authState ->
      if (authState.isLoading) {
        progressBar.visibility = View.VISIBLE
      } else {
        progressBar.visibility = View.GONE
      }
      when(authState.authResponse) {
        is AuthResult.Authorized -> {
          val intent = Intent(this, MainActivity::class.java)
          startActivity(intent)
        }
        is AuthResult.Unauthorized -> {
          if(isFirstRender) {
            isFirstRender = false
            return@observe
          }
          Toast.makeText(this, "Invalid data 🔒", Toast.LENGTH_SHORT).show()
        }
        is AuthResult.UnknownError -> {
          if(isFirstRender) {
            isFirstRender = false
            return@observe
          }
          Toast.makeText(this, "Unknown error happened 😥", Toast.LENGTH_SHORT).show()
        }
      }
    }
  }

  private fun applyListeners() {
    loginButton.setOnClickListener {
      val password = passwordEditText.text.toString()
      val email = emailEditText.text.toString()
      val regex = Regex(".*\\d+.*")
      val hasEmailIncludeChar = email.contains("@")
      if(hasEmailIncludeChar && regex.containsMatchIn(password)) {
        authViewModel.login(email, password)
      } else {
        Toast.makeText(this, "Fields are not valid", Toast.LENGTH_SHORT).show()
      }
    }
    registerTextView.setOnClickListener {
      val intent = Intent(this, RegisterActivity::class.java)
      startActivity(intent)
    }
  }


}