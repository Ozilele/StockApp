package com.example.myapplication
import com.example.myapplication.data.api.StockPriceItem
import com.github.mikephil.charting.data.Entry
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class QuarterStockHandler : StockHandler {

  private val quarterDuration = 90

  override fun updateStockPrice(stockPricesList: List<StockPriceItem>): String {
    val currentStockPrice = stockPricesList[0].price.toFloat()
    val stockPriceQuarterAgo = stockPricesList[quarterDuration - 1].price.toFloat()
    val difference = currentStockPrice - stockPriceQuarterAgo
    return String.format("%.2f", (difference / stockPriceQuarterAgo) * 100)
  }

  override fun applyXAxisChartData(stockPricesList: List<StockPriceItem>): ArrayList<Entry> {
    val entries = arrayListOf<Entry>()
    for(i in 0 until quarterDuration) {
      val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
      val date = dateFormatter.parse(stockPricesList[i].date)
      val daysSinceReference = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(date.time).toFloat()
      val entry = Entry(daysSinceReference, stockPricesList[i].price.toFloat())
      entries.add(entry)
    }
    return entries
  }
}