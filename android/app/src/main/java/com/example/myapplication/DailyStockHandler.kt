package com.example.myapplication

import com.example.myapplication.data.api.StockPriceItem
import com.example.myapplication.data.api.StockPriceResponse
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.Deferred
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class DailyStockHandler : StockHandler {

  suspend fun handleStockPriceResponse(companyStockPrices : Deferred<Response<StockPriceResponse>?>) : List<StockPriceItem> {
    var stockPrices : List<StockPriceItem> = emptyList()
    companyStockPrices.await()?.let { companyStockPrices ->
      if(companyStockPrices.isSuccessful && companyStockPrices.body() != null) {
        println("${companyStockPrices.body()}")
        companyStockPrices.body()?.let { companyStockPrices ->
          val stockPricesMap = companyStockPrices.timeSeries
          stockPrices = stockPricesMap.entries.map { (date, stockPrice) ->
            StockPriceItem(date, stockPrice.closePrice)
          }
        }
      }
    }
    return stockPrices
  }

  override fun updateStockPrice(stockPrices: List<StockPriceItem>) : String {
    val lastPrice = stockPrices[stockPrices.size - 1].price.toFloat()
    val difference = stockPrices[0].price.toFloat() - lastPrice
    val percentChange = String.format("%.2f", (difference / lastPrice) * 100)
    println(percentChange)
    return percentChange
  }

  override fun applyXAxisChartData(stockPrices: List<StockPriceItem>) : ArrayList<Entry> {
    val entries = arrayListOf<Entry>()
    for(stockItem in stockPrices) {
      val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(stockItem.date)
      val calendar = Calendar.getInstance()
      calendar.time = date
      val hour = calendar.get(Calendar.HOUR_OF_DAY).toFloat()
      val minute = calendar.get(Calendar.MINUTE).toFloat()
      val entry = Entry(hour + (minute / 100.0f), stockItem.price.toFloat())
      entries.add(entry)
    }
    return entries
  }
}