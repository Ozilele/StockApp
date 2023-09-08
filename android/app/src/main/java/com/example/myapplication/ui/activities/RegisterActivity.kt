package com.example.myapplication.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.myapplication.R
import com.example.myapplication.data.backend.auth.AuthResult
import com.example.myapplication.databinding.ActivityRegisterBinding
import com.example.myapplication.viewModel.AuthViewModel
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

  private lateinit var binding: ActivityRegisterBinding
  private lateinit var progressBar: ProgressBar
  private lateinit var nameEditText: TextInputEditText
  private lateinit var emailEditText: TextInputEditText
  private lateinit var passwordEditText: TextInputEditText
  private lateinit var repeatPasswordEditText: TextInputEditText
  private lateinit var signUpButton: AppCompatButton
  @Inject
  lateinit var viewModel: AuthViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityRegisterBinding.inflate(layoutInflater)
    setContentView(binding.root)
    binding.apply {
      this@RegisterActivity.progressBar = progressBar
      nameEditText = nameInputEditText
      emailEditText = emailInputEditText
      passwordEditText = passwordInputEditText
      repeatPasswordEditText = passwordRepeatInputEditText
      this@RegisterActivity.signUpButton = signUpButton
    }
    observeViewModel()
    applyListeners()
  }

  private fun observeViewModel() {
    viewModel.authState.observe(this) { authState ->
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
        is AuthResult.Unauthorized -> return@observe
        is AuthResult.UnknownError -> return@observe
      }
    }
  }

  private fun applyListeners() {
    signUpButton.setOnClickListener {
      val name = nameEditText.text.toString()
      val email = emailEditText.text.toString()
      val password = passwordEditText.text.toString()
      val passwordRepeat = repeatPasswordEditText.text.toString()
      val regex = Regex(".*\\d+.*")
      val hasEmailIncludeChar = email.contains("@")
      if((password == passwordRepeat) && hasEmailIncludeChar && regex.containsMatchIn(password)) {
        viewModel.register(name, email, password)
      } else {
        Toast.makeText(this, "Invalid fields", Toast.LENGTH_SHORT).show()
      }
    }
  }


}