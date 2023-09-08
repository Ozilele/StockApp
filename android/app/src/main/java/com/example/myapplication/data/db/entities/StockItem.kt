package com.example.myapplication.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "acquired_stocks")
data class StockItem(
  @PrimaryKey(autoGenerate = true) val stock_id: Int = 0,
  val symbol : String,
  val name : String,
  val quantity : Float,
  val currentPrice : Float,
  val value : Float, // price per 1 stock when user buyed this stock
)
