package com.example.myapplication.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplication.data.db.entities.StockListing

@Dao
interface StockDao {

  @Query(
    """
    SELECT * 
    FROM stocks 
    WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%' OR 
      UPPER(:query) == symbol
    """)
  suspend fun getStockListings(query: String) : List<StockListing>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertStockListings(stockListingEntities: List<StockListing>)

  @Query("DELETE FROM stocks")
  suspend fun clearStockListings()

}