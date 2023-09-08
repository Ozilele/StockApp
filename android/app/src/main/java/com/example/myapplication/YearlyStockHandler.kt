package com.example.myapplication

import com.example.myapplication.data.api.StockPriceItem
import com.example.myapplication.data.api.StockPriceWeeklyResponse
import com.example.myapplication.data.api.StockYearlyPriceResponse
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.Deferred
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class YearlyStockHandler : StockHandler {

  private var weeks = 0

  suspend fun handleStockPriceResponse(companyStockPrices : Deferred<Response<StockYearlyPriceResponse>?>) : List<StockPriceItem> {
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

  override fun updateStockPrice(pricesList: List<StockPriceItem>): String {
    val currentDate = pricesList[0].date
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val inputDate = LocalDate.parse(currentDate, dateFormatter)

    val dateOneYearAgo = inputDate.minusYears(1)
    var currDate = dateOneYearAgo
    while(currDate.isBefore(inputDate) || currDate == inputDate) {
      weeks++
      currDate = currDate.plusWeeks(1)
    }
    println("Counted $weeks weeks")
    val stockCurrentPrice = pricesList[0].price.toFloat()
    val stockPriceYearAgo = pricesList[weeks - 1].price.toFloat()
    val difference = stockCurrentPrice - stockPriceYearAgo
    return String.format("%.2f", (difference / stockPriceYearAgo) * 100)
  }

  override fun applyXAxisChartData(pricesList: List<StockPriceItem>): ArrayList<Entry> {
    val entries = arrayListOf<Entry>()
    for(i in 0 until weeks) {
      val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
      val date = dateFormatter.parse(pricesList[i].date)
      val daysSinceReference = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(date.time).toFloat()
      val entry = Entry(daysSinceReference, pricesList[i].price.toFloat())
      entries.add(entry)
    }
    return entries
  }
}