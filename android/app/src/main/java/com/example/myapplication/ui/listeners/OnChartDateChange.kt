package com.example.myapplication.ui.listeners

import com.example.myapplication.data.FilterType

interface OnChartDateChange {
  fun onChartDateClick(filterType: FilterType)
}