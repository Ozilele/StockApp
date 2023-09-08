package com.example.myapplication

import com.example.myapplication.data.api.StockPriceItem
import com.example.myapplication.data.api.StockPriceWeeklyResponse
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.Deferred
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class WeeklyStockHandler : StockHandler {

  private var dataCount : Int = 0

  suspend fun handleStockPriceResponse(companyStockPrices : Deferred<Response<StockPriceWeeklyResponse>?>) : List<StockPriceItem> {
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
    val currentDate = stockPrices[0].date
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val inputDate = LocalDate.parse(currentDate, dateFormatter)
    val dateWeekAgo = inputDate.minusWeeks(1)
    for(stockItem in stockPrices) {
      val date = LocalDate.parse(stockItem.date, dateFormatter)
      if(date.dayOfMonth > dateWeekAgo.dayOfMonth) {
        dataCount++
      }
    }
    val currentStockPrice = stockPrices[0].price.toFloat()
    val stockPriceWeekAgo = stockPrices[dataCount].price.toFloat()
    val difference = currentStockPrice - stockPriceWeekAgo
    return String.format("%.2f", (difference / stockPriceWeekAgo) * 100)
  }

  override fun applyXAxisChartData(stockPrices: List<StockPriceItem>) : ArrayList<Entry> {
    val entries = arrayListOf<Entry>()
    for(i in dataCount - 1 downTo  0) {
      val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
      val date = dateFormatter.parse(stockPrices[i].date)
      val hoursReference = java.util.concurrent.TimeUnit.MILLISECONDS.toHours(date.time).toFloat()
      println("Hours ref: $hoursReference")
      val entry = Entry(hoursReference, stockPrices[i].price.toFloat())
      entries.add(entry)
    }
    return entries
  }

}