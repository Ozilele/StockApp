package com.example.myapplication

import com.example.myapplication.data.api.StockMonthlyPriceResponse
import com.example.myapplication.data.api.StockPriceItem
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.Deferred
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class MonthlyStockHandler : StockHandler {

  private var workingDays : Int = 0

  suspend fun handleStockPriceResponse(companyStockPrices : Deferred<Response<StockMonthlyPriceResponse>?>) : List<StockPriceItem> {
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
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val inputDate = LocalDate.parse(currentDate, dateFormatter)
    val dateOneMonthAgo = inputDate.minusMonths(1)
    // Counting working days
    var currDate = dateOneMonthAgo
    while(currDate.isBefore(inputDate) || currDate == inputDate) {
      if(currDate.dayOfWeek.value < 6) {
        workingDays++
      }
      currDate = currDate.plusDays(1)
    }
    val currentStockPrice = stockPrices[0].price.toFloat()
    val stockPriceOneMonthAgo = stockPrices[workingDays - 1].price.toFloat()
    val difference = currentStockPrice - stockPriceOneMonthAgo
    return String.format("%.2f", (difference / stockPriceOneMonthAgo) * 100)
  }

  override fun applyXAxisChartData(stockPrices: List<StockPriceItem>) : ArrayList<Entry> {
    val entries = arrayListOf<Entry>()
    for(i in workingDays - 1 downTo  0) {
      val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
      val date = dateFormatter.parse(stockPrices[i].date)
      val daysSinceReference = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(date.time).toFloat()
      val entry = Entry(daysSinceReference, stockPrices[i].price.toFloat())
      entries.add(entry)
    }
    return entries
  }
}