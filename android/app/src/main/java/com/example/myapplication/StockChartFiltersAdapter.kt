package com.example.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.FilterType
import com.example.myapplication.databinding.StockChartFilterItemBinding
import com.example.myapplication.ui.listeners.OnChartDateChange

class StockChartFiltersAdapter(private val filtersList : List<FilterType>, private val listener : OnChartDateChange, private var currentFilter : FilterType) : RecyclerView.Adapter<StockChartFiltersAdapter.StockChartFiltersViewHolder>() {

  fun setCurrentFilter(filterType: FilterType) {
    currentFilter = filterType
    notifyDataSetChanged()
  }

  inner class StockChartFiltersViewHolder(val binding : StockChartFilterItemBinding) : RecyclerView.ViewHolder(binding.root) {

  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockChartFiltersViewHolder {
    return StockChartFiltersViewHolder(StockChartFilterItemBinding.inflate(
      LayoutInflater.from(parent.context),
      parent,
      false
    ))
  }

  override fun onBindViewHolder(holder: StockChartFiltersViewHolder, position: Int) {
    val currentFilterType = filtersList[position]

    holder.binding.apply {
      textViewChartFilter.text = currentFilterType.text
      for(filter in filtersList) {
        textViewChartFilter.setBackgroundResource(R.drawable.background_stock_chart_filter)
      }
      if(textViewChartFilter.text == currentFilter.text) {
        textViewChartFilter.setBackgroundResource(R.drawable.background_stock_chart_filter_active)
      }
    }
    holder.binding.textViewChartFilter.setOnClickListener {
      listener.onChartDateClick(currentFilterType)
    }
  }

  override fun getItemCount(): Int {
    return filtersList.size
  }

}