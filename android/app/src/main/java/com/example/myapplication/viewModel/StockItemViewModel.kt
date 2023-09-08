package com.example.myapplication.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.*
import com.example.myapplication.data.ApiRepository
import com.example.myapplication.data.FilterType
import com.example.myapplication.data.api.ApiStockPriceResponse
import com.example.myapplication.data.api.CompanyOverview
import com.example.myapplication.data.api.StockPriceItem
import com.github.mikephil.charting.data.Entry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class StockItemViewModel @Inject constructor(
  private val retrofitInstance : StockApi,
  private val stockApiRepository: ApiRepository
) : ViewModel() {

  private var currentChartFilter = MutableLiveData<FilterType>() // current chart filter
  private var companyInfo = MutableLiveData<CompanyOverview>()
  private var chartListOfEntries = MutableLiveData<ArrayList<Entry>>() // current list of entries for chart
  var stockPrices = MutableLiveData<List<StockPriceItem>>() // stock price items
  var stockPercentChange = MutableLiveData<String>() // percent change of stock value based on current chart viewed

  suspend fun getCompanyInfo(symbol : String) : Response<CompanyOverview>? {
    var data : Response<CompanyOverview>? = null
    try {
      data = retrofitInstance.getCompanyInfo(symbol = symbol)
    } catch(e: IOException) {
      e.printStackTrace()
    } catch(e: HttpException) {
      e.printStackTrace()
    }
    return data
  }

  suspend fun handleCompanyInfoResponse(companyInfoResponse : Deferred<Response<CompanyOverview>?>) {
    companyInfoResponse.await()?.let { companyInfoResponse ->
      if(companyInfoResponse.isSuccessful && companyInfoResponse.body() != null) {
        println("${companyInfoResponse.body()}")
        companyInfoResponse.body()?.let { companyOverview ->
          withContext(Dispatchers.Main) {
            companyInfo.value = companyOverview
          }
        }
      }
    }
  }

  suspend fun <T: ApiStockPriceResponse> getStockPrices(symbol : String, interval : FilterType) : Response<T>? {
    withContext(Dispatchers.Main) {
      currentChartFilter.value = interval // update chart filter
    }
    return stockApiRepository.getStockPrices<T>(symbol, interval)?.let { response ->
      response
    }
  }

  fun updateStockPricesList(stockPricesList : List<StockPriceItem>, stockHandler : StockHandler) {
    stockPrices.value = stockPricesList
    stockPercentChange.value = stockPrices.value?.let { stockHandler.updateStockPrice(it) }
    chartListOfEntries.value = stockPrices.value?.let { stockHandler.applyXAxisChartData(it) }
  }

  fun filter() : LiveData<FilterType> {
    return currentChartFilter
  }

  fun companyInfo() : LiveData<CompanyOverview> {
    return companyInfo
  }

  fun chartEntries() : LiveData<ArrayList<Entry>> {
    return chartListOfEntries
  }

}