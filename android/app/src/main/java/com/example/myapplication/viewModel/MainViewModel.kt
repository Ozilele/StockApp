package com.example.myapplication.viewModel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.StockApi
import com.example.myapplication.data.api.StockSearch
import com.example.myapplication.data.csv.StockListingsParser
import com.example.myapplication.data.db.StockDatabase
import com.example.myapplication.data.db.entities.StockListing
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
  private val db : StockDatabase,
  private val retrofitInstance : StockApi,
) : ViewModel() {

  private var darkNightMode = MutableLiveData<Int>()
  val nightMode : LiveData<Int> get() = darkNightMode
  private var stockSearchList = MutableLiveData<List<StockSearch>>()
  private var bottomAppBarVisibility = MutableLiveData<Int>()
  val visibilityBottomAppBar: LiveData<Int> get() = bottomAppBarVisibility
  private var floatingActionButtonVisibility = MutableLiveData<Int>()
  val visibilityFloatingActionButton: LiveData<Int> get() = floatingActionButtonVisibility

  fun setDarkNightMode(mode : Int) {
    darkNightMode.value = mode
  }

  fun setVisibility(hasFocus : Boolean) {
    if(hasFocus) {
      bottomAppBarVisibility.value = View.GONE
      floatingActionButtonVisibility.value = View.GONE
    } else {
      bottomAppBarVisibility.value = View.VISIBLE
      floatingActionButtonVisibility.value = View.VISIBLE
    }
  }

  fun setBottomAppBarVisibility(status: Int) {
    bottomAppBarVisibility.value = status
    floatingActionButtonVisibility.value = status
  }

  fun readCsvAndInsertDb() {
    CoroutineScope(Dispatchers.IO).launch {
      val remoteStockListings = try {
        val response = retrofitInstance.getListings()
        StockListingsParser().parse(response.byteStream())
      } catch(e: IOException) {
        e.printStackTrace()
        null
      } catch(e: HttpException) {
        e.printStackTrace()
        null
      }
      stockSearchList.value = remoteStockListings!!
      println("Size danych otrzymanych z csv jest równy ${remoteStockListings?.size}")
      remoteStockListings?.let {
        db.stockDao.insertStockListings(it.map { obj->
          StockListing(symbol = obj.symbol, name = obj.name, exchange = obj.exchange)
        })
      }
    }
  }

}