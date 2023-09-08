package com.example.myapplication.ui.fragments

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.ui.listeners.ItemInterface
import com.example.myapplication.R
import com.example.myapplication.StockAdapter
import com.example.myapplication.StockApi
import com.example.myapplication.data.db.StockDatabase
import com.example.myapplication.data.db.entities.StockListing
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.ui.activities.MainActivity
import com.example.myapplication.ui.activities.StockItemActivity
import com.example.myapplication.ui.activities.TAG
import com.example.myapplication.viewModel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class HomeFragment : Fragment(), ItemInterface {

  private val CHANNEL_id = "myChannelID"
  private val CHANNEL_name = "myChannelName"
  private val NOTIFICATION_ID = 0
  @Inject
  lateinit var db : StockDatabase
  @Inject
  lateinit var retrofitInstance : StockApi

  @Inject
  @Named("Preferences Mode")
  lateinit var modePreferences: SharedPreferences

  private val mainViewModel: MainViewModel by activityViewModels()
  private lateinit var binding : FragmentHomeBinding
  private lateinit var searchView: androidx.appcompat.widget.SearchView
  private lateinit var modeSwitch : SwitchCompat
  private lateinit var notificationBtn : ImageButton
  private lateinit var stockAdapter: RecyclerView
  private lateinit var stockListings : List<StockListing>

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    super.onCreateView(inflater, container, savedInstanceState)
    binding = FragmentHomeBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.apply {
      stockAdapter = recyclerViewStocks
      this@HomeFragment.modeSwitch = modeSwitch
      this@HomeFragment.notificationBtn = notificationBtn
      this@HomeFragment.searchView = searchView
    }
    initializeMode()
    createNotificationChannel()
    setupRecyclerView()
    applyListeners()
    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(requireContext(), 2, intent, PendingIntent.FLAG_IMMUTABLE)

    val notification = NotificationCompat.Builder(requireContext(), CHANNEL_id)
      .setContentTitle("My first notification")
      .setContentText("Hello world")
      .setSmallIcon(R.drawable.ic_chart)
      .setContentIntent(pendingIntent)
      .setPriority(NotificationCompat.PRIORITY_DEFAULT)
      .build()

    val notificationManager = NotificationManagerCompat.from(requireContext())
    notificationBtn.setOnClickListener {
      val permissionStatus = checkSelfPermission( requireContext(), android.Manifest.permission.POST_NOTIFICATIONS)
      if(permissionStatus != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(
          requireActivity(),
          arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
          2
        )
      } else {
        notificationManager.notify(NOTIFICATION_ID, notification)
      }
    }
    getListingsData("")
  }

  private fun initializeMode() {
    modeSwitch.isChecked = modePreferences.getBoolean("nightMode", true)
    modeSwitch.setOnCheckedChangeListener { _, isChecked ->
      if(!isChecked) {
        modeSwitch.isChecked = false
        mainViewModel.setDarkNightMode(AppCompatDelegate.MODE_NIGHT_NO)
      } else { // change to dark mode
        modeSwitch.isChecked = true
        mainViewModel.setDarkNightMode(AppCompatDelegate.MODE_NIGHT_YES)
      }
    }
  }

  private fun createNotificationChannel() {
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val channel = NotificationChannel(CHANNEL_id, CHANNEL_name, NotificationManager.IMPORTANCE_DEFAULT).apply {
        lightColor = Color.GREEN
        enableLights(true)
      }
      val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.createNotificationChannel(channel)
    }
  }

  private fun setupRecyclerView() {
    stockAdapter.layoutManager = LinearLayoutManager(context)
  }

  private fun applyListeners() {
    searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
      if(hasFocus) {
        searchView.setBackgroundResource(R.drawable.search_view_bg_focus)
        mainViewModel.setVisibility(true)
      } else {
        searchView.setBackgroundResource(R.drawable.search_view_bg)
        mainViewModel.setVisibility(false)
      }
    }
    searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
      override fun onQueryTextSubmit(query: String?): Boolean {
        // Network request to get stocks passing the query
        searchView.clearFocus()
        CoroutineScope(Dispatchers.IO).launch {
          Log.d(TAG, "Coroutine says hello from thread: ${Thread.currentThread().name}")
          val response = try {
            retrofitInstance.getStockMatches(keywords = query!!)
          } catch(e: IOException) {
            Log.e(TAG, "IOException, you might not have internet connection")
            return@launch
          } catch(e: HttpException) {
            Log.e(TAG, "HttpException, unexpected response")
            return@launch
          }
          if(response.isSuccessful && response.body() != null) {
            println(response.body())
          }
        }
        return false
      }

      override fun onQueryTextChange(newText: String?): Boolean {
        getListingsData(newText)
        return false
      }
    })
  }

  private fun getListingsData(query : String?) {
    CoroutineScope(Dispatchers.IO).launch {
      stockListings = db.stockDao.getStockListings(query!!)
      println(stockListings.size)
      withContext(Dispatchers.Main) {
        stockAdapter.adapter = StockAdapter(stockListings, this@HomeFragment, requireContext())
      }
    }
  }

  override fun onClick(item: StockListing) {
    val newIntent = Intent(context, StockItemActivity::class.java)
    newIntent.putExtra("company_symbol", item.symbol)
    startActivity(newIntent)
  }
}