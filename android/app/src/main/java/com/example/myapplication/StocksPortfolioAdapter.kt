package com.example.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.db.entities.StockItem
import com.example.myapplication.databinding.PortfolioStockItemBinding
import kotlin.math.roundToInt

class StocksPortfolioAdapter(private var acquiredStocksList : List<StockItem>) : RecyclerView.Adapter<StocksPortfolioAdapter.StocksPortfolioViewHolder>() {

  fun setAdapterListData(newStockList : List<StockItem>) {
    acquiredStocksList = newStockList
    notifyDataSetChanged()
  }

  inner class StocksPortfolioViewHolder(val binding : PortfolioStockItemBinding) : RecyclerView.ViewHolder(binding.root) {

  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StocksPortfolioViewHolder {
    return StocksPortfolioViewHolder(PortfolioStockItemBinding.inflate(
      LayoutInflater.from(parent.context),
      parent,
      false
    ))
  }

  override fun onBindViewHolder(holder: StocksPortfolioViewHolder, position: Int) {
    val stockItem = acquiredStocksList[position]
//    stockItem.currentPrice - aktualna cena za jedną akcję
    // stockItem.value - cena kupna akcji firmy(jakiejś ilości)
    val stockPercentChangeValue = (((stockItem.quantity * stockItem.currentPrice) - stockItem.value) / stockItem.value) * 100
    holder.binding.apply {
      portfolioStockName.text = stockItem.name
      portfolioStockSymbol.text = stockItem.symbol
      portfolioStockValue.text = "${String.format("%.2f", (stockItem.quantity * stockItem.currentPrice))}$"
      stockQuantity.text = "x ${stockItem.quantity.roundToInt()}"
      portfolioStockPercentChange.text = "(${String.format("%.2f", stockPercentChangeValue)}%)"
      if(stockPercentChangeValue < 0) {
        portfolioStockPercentIcon.setImageResource(R.drawable.portfolio_arrow_down)
        portfolioStockPercentIcon.setBackgroundResource(R.drawable.portfolio_stock_percent_bg_negative)
      }
    }
  }

  override fun getItemCount(): Int {
    return acquiredStocksList.size
  }

}