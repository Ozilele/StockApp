package com.example.myapplication.ui.activities

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.*
import com.example.myapplication.data.FilterType
import com.example.myapplication.data.api.StockMonthlyPriceResponse
import com.example.myapplication.data.api.StockPriceResponse
import com.example.myapplication.data.api.StockPriceWeeklyResponse
import com.example.myapplication.data.api.StockYearlyPriceResponse
import com.example.myapplication.databinding.ActivityStockItemBinding
import com.example.myapplication.ui.formatters.MonthlyCustomAxisFormatter
import com.example.myapplication.ui.formatters.WeeklyCustomAxisFormatter
import com.example.myapplication.ui.formatters.YearlyCustomAxisFormatter
import com.example.myapplication.ui.listeners.OnChartDateChange
import com.example.myapplication.viewModel.StockItemViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.EntryXComparator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.util.*

@AndroidEntryPoint
class StockItemActivity : AppCompatActivity(), OnChartDateChange {

  private var symbol : String? = null
  private lateinit var binding : ActivityStockItemBinding
  private lateinit var btnBack : Button
  private lateinit var stockChartsFilterAdapter : StockChartFiltersAdapter
  private lateinit var stockDescriptionView : TextView
  private lateinit var stock52WeekHigh : TextView
  private lateinit var stock52WeekLow : TextView
  private lateinit var stockSymbolView : TextView
  private lateinit var stockNameView : TextView
  private lateinit var stockPercentChangeView : TextView
  private lateinit var stockCurrentPriceView : TextView
  private lateinit var companyIndustryView : TextView
  private lateinit var companyCountryView : TextView
  private lateinit var lineChart : LineChart
  private lateinit var recyclerViewFilters : RecyclerView
  private val viewModel : StockItemViewModel by viewModels()
  private val listOfFilters = listOf(FilterType.DAILY, FilterType.WEEKLY, FilterType.MONTHLY, FilterType.MONTHLY_3, FilterType.YEARLY)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityStockItemBinding.inflate(layoutInflater)
    setContentView(binding.root)
    symbol = intent.getStringExtra("company_symbol")
    binding.apply {
      this@StockItemActivity.btnBack = btnBack
      this@StockItemActivity.recyclerViewFilters = recyclerViewFilters
      stockDescriptionView = companyDescription
      stockSymbolView = stockSymbol
      stockNameView = stockName
      stockPercentChangeView = stockPercentChange
      stockCurrentPriceView = stockCurrentPrice
      companyIndustryView = companyIndustry
      companyCountryView = companyCountry
      lineChart = stockLineChart
      this@StockItemActivity.stock52WeekHigh = stock52weekHigh
      this@StockItemActivity.stock52WeekLow = stock52weekLow
    }
    btnBack.setOnClickListener {
      super.onBackPressed()
    }
    recyclerViewFilters.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    stockChartsFilterAdapter = StockChartFiltersAdapter(listOfFilters, this, FilterType.DAILY)
    recyclerViewFilters.adapter = stockChartsFilterAdapter
    observeData()
    makeNetworkCall(symbol!!)
  }

  private fun observeData() {
    viewModel.filter().observe(this, androidx.lifecycle.Observer {
      stockChartsFilterAdapter.setCurrentFilter(viewModel.filter().value!!)
    })
    viewModel.companyInfo().observe(this, androidx.lifecycle.Observer { companyOverview ->
      stockDescriptionView.text = companyOverview.Description
      stockNameView.text = companyOverview.Name
      stockSymbolView.text = companyOverview.Symbol
      stock52WeekHigh.text = "High(52 week): ${companyOverview.week52High}$"
      stock52WeekLow.text = "Low(52 week): ${companyOverview.week52Low}$"
      companyCountryView.text = "Country: ${companyOverview.Country}"
      companyIndustryView.text = "Industry: ${companyOverview.Industry}"
    })
  }

  @SuppressLint("ResourceAsColor")
  private fun makeNetworkCall(symbol : String) {
    CoroutineScope(Dispatchers.IO).launch {
      val companyInfoResponse = async {
        viewModel.getCompanyInfo(symbol)
      }
      val companyStockPrices = async {
        viewModel.getStockPrices<StockPriceResponse>(symbol, FilterType.DAILY)
      }
      viewModel.handleCompanyInfoResponse(companyInfoResponse)
      val stockHandler = DailyStockHandler()
      val stockPricesList = stockHandler.handleStockPriceResponse(companyStockPrices)
      withContext(Dispatchers.Main) {
        viewModel.updateStockPricesList(stockPricesList, stockHandler)
      }
      handleStockPriceResponse( "1D")
    }
  }

  private suspend fun handleStockPriceResponse(info : String) {
    withContext(Dispatchers.Main) {
      stockCurrentPriceView.text = String.format("%.2f", viewModel.stockPrices.value?.get(0)!!.price.toFloat()) + "$"
      stockPercentChangeView.text = "(${viewModel.stockPercentChange.value}%) ${viewModel.filter().value!!.text}"
      if(viewModel.stockPercentChange.value!!.toFloat() < 0) {
        stockPercentChangeView.setTextColor(Color.RED)
      } else {
        stockPercentChangeView.setTextColor(Color.GREEN)
      }
      setChartData(info)
    }
  }

  // Chart data differs of the chart type
  private fun setChartData(info: String) {
    val entries = viewModel.chartEntries().value
    Collections.sort(entries, EntryXComparator())

    val dataset = LineDataSet(entries, "Stock's Daily Chart")
    dataset.setDrawFilled(true)
    dataset.setDrawValues(false)
    dataset.fillColor = ContextCompat.getColor(this, R.color.chart_color)
    dataset.color = ContextCompat.getColor(this, R.color.chart_color)
    if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
      dataset.fillAlpha = 35
    } else {
      dataset.fillAlpha = 180
    }

    dataset.setDrawCircles(false)
    val lineData = LineData(dataset)
    lineChart.data = lineData

    var xAxis = lineChart.xAxis
    xAxis.position = XAxis.XAxisPosition.BOTTOM
    xAxis.setDrawGridLines(false)
    xAxis.setDrawLabels(true)
    xAxis.setDrawAxisLine(true)
    if(info == "1M" || info == "3M") {
      xAxis.valueFormatter = MonthlyCustomAxisFormatter()
    }
    if(info == "7D") {
      xAxis.valueFormatter = WeeklyCustomAxisFormatter()
    }
    if(info == "1Y") {
      xAxis.valueFormatter = YearlyCustomAxisFormatter()
    }
    xAxis.textColor = ContextCompat.getColor(this, R.color.stock_info_color)

    val yAxis = lineChart.axisLeft
    yAxis.textColor = ContextCompat.getColor(this, R.color.stock_info_color)
    yAxis.gridColor = Color.DKGRAY

    lineChart.setTouchEnabled(true)
    lineChart.setPinchZoom(true)
    lineChart.description.text = ""
    lineChart.axisRight.isEnabled = false
    lineChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
      override fun onValueSelected(e: Entry?, h: Highlight?) {
        if (e != null) {
          val index = e.x
          println(index)
        }
      }
      override fun onNothingSelected() {
        return
      }
    })
    lineChart.invalidate()
  }

  override fun onChartDateClick(filterType: FilterType) {
    when(filterType) {
      FilterType.DAILY -> {
        CoroutineScope(Dispatchers.IO).launch {
          val companyStockPrices = async { viewModel.getStockPrices<StockPriceResponse>(symbol!!, FilterType.DAILY) }
          val stockHandler = DailyStockHandler()
          val stockPricesList = stockHandler.handleStockPriceResponse(companyStockPrices)
          withContext(Dispatchers.Main) { // ViewModel Update
            viewModel.updateStockPricesList(stockPricesList, stockHandler)
          }
          handleStockPriceResponse("1D")
        }
      }
      FilterType.WEEKLY -> {
        CoroutineScope(Dispatchers.IO).launch {
          val weeklyStockPrices = async {
            viewModel.getStockPrices<StockPriceWeeklyResponse>(symbol!!, FilterType.WEEKLY)
          }
          val stockHandler = WeeklyStockHandler()
          val stockPricesList = stockHandler.handleStockPriceResponse(weeklyStockPrices)
          withContext(Dispatchers.Main) { // ViewModel Update
            viewModel.updateStockPricesList(stockPricesList, stockHandler)
          }
          handleStockPriceResponse("7D")
        }
      }
      FilterType.MONTHLY -> {
        CoroutineScope(Dispatchers.IO).launch {
          val monthlyStockPrices = async {
            viewModel.getStockPrices<StockMonthlyPriceResponse>(symbol!!, FilterType.MONTHLY)
          }
          val stockHandler = MonthlyStockHandler()
          val stockPricesList = stockHandler.handleStockPriceResponse(monthlyStockPrices)
          withContext(Dispatchers.Main) { // ViewModel Update
            viewModel.updateStockPricesList(stockPricesList, stockHandler)
          }
          handleStockPriceResponse("1M")
        }
      }
      FilterType.MONTHLY_3 -> {
        CoroutineScope(Dispatchers.IO).launch {
          val quarterStockPrices = async {
            viewModel.getStockPrices<StockMonthlyPriceResponse>(symbol!!, FilterType.MONTHLY_3)
          }
          val stockHandler = MonthlyStockHandler()
          val stockPricesList = stockHandler.handleStockPriceResponse(quarterStockPrices)
          withContext(Dispatchers.Main) {
            viewModel.updateStockPricesList(stockPricesList, QuarterStockHandler())
          }
          handleStockPriceResponse("3M")
        }
      }
      FilterType.YEARLY -> {
        CoroutineScope(Dispatchers.IO).launch {
          val yearStockPrices = async {
            viewModel.getStockPrices<StockYearlyPriceResponse>(symbol!!, FilterType.YEARLY)
          }
          val stockHandler = YearlyStockHandler()
          val stockPricesList = stockHandler.handleStockPriceResponse(yearStockPrices)
          withContext(Dispatchers.Main) {
            viewModel.updateStockPricesList(stockPricesList, stockHandler)
          }
          handleStockPriceResponse("1Y")
        }
      }
    }
  }

}