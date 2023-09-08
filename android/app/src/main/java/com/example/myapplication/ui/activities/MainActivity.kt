package com.example.myapplication.ui.activities


import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.data.db.StockDatabase
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.ui.fragments.HomeFragment
import com.example.myapplication.ui.fragments.PortfolioFragment
import com.example.myapplication.viewModel.MainViewModel
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity: AppCompatActivity() {

  private lateinit var bottomAppBar : BottomAppBar
  private lateinit var bottomNavigationView : BottomNavigationView
  private lateinit var floatingActionButton : FloatingActionButton
  private lateinit var fragmentContainer : FragmentContainerView
  private lateinit var binding: ActivityMainBinding
  private val mainViewModel: MainViewModel by viewModels()
  private lateinit var editor: Editor

  @Inject
  @Named("Preferences Mode")
  lateinit var modePreferences: SharedPreferences

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    binding.apply {
      floatingActionButton = floatingActionBtn
      this@MainActivity.fragmentContainer = fragmentContainer
      this@MainActivity.bottomAppBar = bottomAppBar
      this@MainActivity.bottomNavigationView = bottomNavigationView
    }
    editor = modePreferences.edit()
    observeData()

    bottomNavigationView.background = null
    bottomNavigationView.menu.getItem(2).isEnabled = false

    supportFragmentManager.beginTransaction().apply {
      replace(fragmentContainer.id, HomeFragment())
    }.commit()

    bottomNavigationView.setOnItemSelectedListener { menuItem -> // Fragments for each section
      when(menuItem.itemId) {
        R.id.home -> {
          replaceFragment(HomeFragment())
          return@setOnItemSelectedListener true
        }
        R.id.chart -> {
          replaceFragment(PortfolioFragment())
          return@setOnItemSelectedListener true
        }
        R.id.news -> {
          return@setOnItemSelectedListener true
        }
        R.id.settings -> {
          return@setOnItemSelectedListener true
        }
      }
      false
    }
  }

  override fun onBackPressed() {
    Toast.makeText(this, "You are not allowed to return", Toast.LENGTH_SHORT).show()
  }

  private fun observeData() {
    mainViewModel.visibilityBottomAppBar.observe(this, Observer {
      bottomAppBar.visibility = mainViewModel.visibilityBottomAppBar.value!!
    })
    mainViewModel.visibilityFloatingActionButton.observe(this, Observer {
      floatingActionButton.visibility = mainViewModel.visibilityFloatingActionButton.value!!
    })
    mainViewModel.nightMode.observe(this, Observer {
      if(mainViewModel.nightMode.value!! == AppCompatDelegate.MODE_NIGHT_YES) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        editor.putBoolean("nightMode", true)
      } else {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        editor.putBoolean("nightMode", false)
      }
      editor.apply()
    })
  }

  private fun replaceFragment(fragment: Fragment) {
    val fragmentManager = supportFragmentManager
    fragmentManager.beginTransaction().apply {
      replace(fragmentContainer.id, fragment)
    }.commit()
  }
}