package com.example.myapplication.ui.listeners

import com.example.myapplication.data.db.entities.StockListing

interface ItemInterface {
  fun onClick(item : StockListing)
}