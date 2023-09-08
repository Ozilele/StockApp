package com.example.myapplication.data.db

import android.os.Parcelable
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.myapplication.data.db.dao.StockDao
import com.example.myapplication.data.db.dao.StockItemDao
import com.example.myapplication.data.db.entities.StockItem
import com.example.myapplication.data.db.entities.StockListing

@Database(entities = [StockListing::class, StockItem::class], version = 2)
abstract class StockDatabase : RoomDatabase() {
  abstract val stockDao : StockDao
  abstract val stockItemDao : StockItemDao
}