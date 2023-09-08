package com.example.myapplication.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.StocksPortfolioAdapter
import com.example.myapplication.data.db.StockDatabase
import com.example.myapplication.data.db.StockValue
import com.example.myapplication.data.db.entities.StockItem
import com.example.myapplication.databinding.FragmentPortfolioBinding
import com.example.myapplication.viewModel.MainViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class PortfolioFragment : Fragment(R.layout.fragment_portfolio) {

  @Inject
  lateinit var db : StockDatabase
  private lateinit var binding : FragmentPortfolioBinding
  private lateinit var pieChart: PieChart
  private lateinit var progressBar : ProgressBar
  private val mainViewModel: MainViewModel by activityViewModels()
  private lateinit var recyclerViewPortfolio : RecyclerView
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = FragmentPortfolioBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.apply {
      pieChart = portfolioPieChart
      this@PortfolioFragment.recyclerViewPortfolio = recyclerViewPortfolio
      progressBar = chartProgressBar
    }
    progressBar.visibility = View.VISIBLE
    initializeAdapter()
    drawPieChart()
  }

  private fun initializeAdapter() {
    val adapter = StocksPortfolioAdapter(emptyList())
    recyclerViewPortfolio.addOnScrollListener(object : RecyclerView.OnScrollListener() {
      override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (dy > 0) {
          mainViewModel.setBottomAppBarVisibility(View.GONE)
        } else {
          mainViewModel.setBottomAppBarVisibility(View.VISIBLE)
        }
      }
    })
    recyclerViewPortfolio.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    recyclerViewPortfolio.adapter = adapter
    CoroutineScope(Dispatchers.IO).launch {
//      db.stockItemDao.sellStock(9)
//      db.stockItemDao.buyStock(
//        StockItem(symbol = "BTC", name = "Bitcoin", quantity = 1.02f, currentPrice = 1451.25f, value = 1302.56f)
//      )
      val listOfAcquiredStocks = db.stockItemDao.getAcquiredStocks()
      withContext(Dispatchers.Main) {
        adapter.setAdapterListData(listOfAcquiredStocks)
        adapter.notifyDataSetChanged()
      }
    }
  }

  private fun stylePieChart(entries : List<PieEntry>) {
    val pieDataSet = PieDataSet(entries, "stocksDataset")
    pieDataSet.sliceSpace = 3f
    pieDataSet.selectionShift = 5f
    pieDataSet.valueTextSize = 15f

    val colors: ArrayList<Int> = ArrayList()
    colors.add(Color.parseColor("#304567"))
    colors.add(Color.parseColor("#309967"))
    colors.add(Color.parseColor("#476567"))
    colors.add(Color.parseColor("#890567"))
    colors.add(Color.parseColor("#a35567"))
    colors.add(Color.parseColor("#ff5f67"))
    colors.add(Color.parseColor("#3ca567"))
    pieDataSet.colors = colors

    val pieData = PieData(pieDataSet)
    pieData.setDrawValues(true)
    pieData.setValueFormatter(PercentFormatter())

    pieChart.data = pieData
    pieChart.setCenterTextSize(18f)
    pieChart.setCenterTextColor(Color.WHITE)

    pieChart.setUsePercentValues(true);
    pieChart.setHoleColor(Color.BLACK)
    pieChart.extraTopOffset = 10f
    pieChart.extraBottomOffset = 5f
    pieChart.extraLeftOffset = 5f
    pieChart.extraRightOffset = 5f
    pieChart.legend.isEnabled = false
    pieChart.visibility = View.VISIBLE
    progressBar.visibility = View.GONE
    pieChart.animateY(1400, Easing.EaseInOutQuad);
  }

  private fun drawPieChart() {
    val entries = arrayListOf<PieEntry>()
    var currentBalance = 0f
    CoroutineScope(Dispatchers.IO).launch {
      val listOfStocksDeferred = async { db.stockItemDao.getCurrentStocksPrice() as ArrayList<StockValue> }
      val listOfStocks = listOfStocksDeferred.await()
      listOfStocks.forEach { stock ->
        currentBalance += stock.currentPrice * stock.quantity
      }
      withContext(Dispatchers.Main) {
        pieChart.centerText = "My stocks\n ${String.format("%.2f", currentBalance)}$"
        for(stock in listOfStocks) {
          val stockCurrValue = String.format("%.2f", stock.currentPrice * stock.quantity).toFloat()
          entries.add(PieEntry(stockCurrValue, stock.name))
        }
        stylePieChart(entries)
      }
    }
  }
}

class PercentFormatter : ValueFormatter() {
  override fun getFormattedValue(value: Float): String {
    return String.format("%.1f%%", value);
  }
}