package com.example.myapplication.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.data.db.StockValue
import com.example.myapplication.data.db.entities.StockItem

@Dao
interface StockItemDao {

  @Query("SELECT * FROM acquired_stocks")
  suspend fun getAcquiredStocks() : List<StockItem>

  @Query("SELECT name,quantity,currentPrice FROM acquired_stocks")
  suspend fun getCurrentStocksPrice() : List<StockValue>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun buyStock(stockItem: StockItem)

  @Query("DELETE FROM acquired_stocks")
  suspend fun sellAllStocks()

  @Query("DELETE FROM acquired_stocks WHERE stock_id = :stock_id")
  suspend fun sellStock(stock_id : Int)

}