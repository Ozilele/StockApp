package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.db.entities.StockListing
import com.example.myapplication.databinding.StockSearchItemBinding
import com.example.myapplication.ui.listeners.ItemInterface

class StockAdapter(private val stockListings : List<StockListing>, private val listener: ItemInterface, private val context : Context) : RecyclerView.Adapter<StockAdapter.StockViewHolder>() {

  inner class StockViewHolder(val binding: StockSearchItemBinding) : RecyclerView.ViewHolder(binding.root) {
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
    return StockViewHolder(StockSearchItemBinding.inflate(
      LayoutInflater.from(parent.context),
      parent,
      false
    ))
  }

  override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
    val stockItemListing = stockListings[position]
    holder.binding.apply {
      stockName.text = stockItemListing.name
      stockSymbol.text = stockItemListing.symbol
      stockExchange.text = stockItemListing.exchange
    }
    holder.binding.stockSearchItem.setOnTouchListener { view, event ->
      when(event.action) {
        MotionEvent.ACTION_DOWN -> {
          view.setBackgroundColor(ContextCompat.getColor(context, R.color.stock_item_hover_color))
          view.setBackgroundResource(R.drawable.border_bottom)
        }
        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
          view.setBackgroundColor(ContextCompat.getColor(context, R.color.primary_color))
          view.setBackgroundResource(R.drawable.border_bottom)
        }
      }
      false
    }

    holder.binding.stockSearchItem.setOnClickListener {
      listener.onClick(stockItemListing)
    }
  }

  override fun getItemCount(): Int {
    return stockListings.size
  }

}