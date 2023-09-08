package com.example.myapplication.ui.activities

import android.app.ActivityOptions
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatDelegate
import com.example.myapplication.R
import com.example.myapplication.data.backend.auth.AuthResult
import com.example.myapplication.databinding.ActivityHomeScreenBinding
import com.example.myapplication.viewModel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class HomeScreenActivity : AppCompatActivity() {

  private lateinit var binding: ActivityHomeScreenBinding
  @Inject
  lateinit var viewModel: AuthViewModel

  @Inject
  @Named("Preferences Mode")
  lateinit var modePreferences: SharedPreferences
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val nightMode = modePreferences.getBoolean("nightMode", true)
    if(nightMode) {
      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }
    binding = ActivityHomeScreenBinding.inflate(layoutInflater)
    setContentView(binding.root)
    viewModel
    CoroutineScope(Dispatchers.Main).launch {
      delay(2250L)
      observeViewModel()
    }
  }

  private fun observeViewModel() {
    viewModel.authState.observe(this) { authState ->
      var newIntent: Intent
      when(authState.authResponse) {
        is AuthResult.Authorized -> {
          newIntent = Intent(this, MainActivity::class.java)
          startActivity(newIntent)
        }
        is AuthResult.Unauthorized -> {
          newIntent = Intent(this, LoginActivity::class.java)
          startActivity(newIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
        is AuthResult.UnknownError -> {
          newIntent = Intent(this, LoginActivity::class.java)
          startActivity(newIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
      }
    }
  }

}