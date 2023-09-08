package com.example.myapplication

import android.widget.TextView
import com.example.myapplication.data.api.StockPriceItem
import com.github.mikephil.charting.data.Entry
import kotlin.collections.ArrayList

interface StockHandler {
  fun updateStockPrice(pricesList : List<StockPriceItem> ) : String
  fun applyXAxisChartData(pricesList: List<StockPriceItem>) : ArrayList<Entry>
}