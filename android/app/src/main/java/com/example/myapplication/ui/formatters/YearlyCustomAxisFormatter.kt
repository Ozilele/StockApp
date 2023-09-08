package com.example.myapplication.ui.formatters

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class YearlyCustomAxisFormatter : ValueFormatter() {

  private val dateFormatter = SimpleDateFormat("MM.yyyy", Locale.getDefault())
  override fun getAxisLabel(value: Float, axis: AxisBase?): String {
    val dateMillis = TimeUnit.DAYS.toMillis(value.toLong())
    val date = Date(dateMillis)
    return dateFormatter.format(date)
  }

}