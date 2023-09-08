package com.example.myapplication.data.api

import com.google.gson.annotations.SerializedName

data class MetaData( // przez serialized mapuję klucze w jsonie do pól data class
  @SerializedName("1: Symbol") val symbol: String,
  @SerializedName("2: Indicator") val indicator: String,
  @SerializedName("3: Last Refreshed") val lastRefreshed: String,
  @SerializedName("4: Interval") val interval: String,
  @SerializedName("5: Time Period") val timePeriod: Int,
  @SerializedName("6: Series Type") val seriesType: String,
  @SerializedName("7: Time Zone") val timeZone: String
)

data class TechnicalAnalysisSMA(
  @SerializedName("SMA") val sma: String,
)

data class StockData(
  @SerializedName("Meta Data") val metaData: MetaData, // data of stock
  @SerializedName("Technical Analysis: SMA") val technicalAnalysisSMA : Map<String, TechnicalAnalysisSMA>
)
