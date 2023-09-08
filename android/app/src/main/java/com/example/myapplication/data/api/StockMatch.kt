package com.example.myapplication.data.api

import com.google.gson.annotations.SerializedName

data class StockMatch(
  @SerializedName("1. symbol") val symbol : String,
  @SerializedName("2. name") val name : String,
  @SerializedName("3. type") val type : String,
  @SerializedName("4. region") val region : String,
  @SerializedName("5. marketOpen") val marketOpenTime : String,
  @SerializedName("6. marketClose") val marketCloseTime : String,
  @SerializedName("7. timezone") val timezone : String,
  @SerializedName("8. currency") val currency : String,
  @SerializedName("9. matchScore") val matchScore : String,
)

data class StockMatchResponse(
  @SerializedName("bestMatches") val listOfMatches : ArrayList<StockMatch>
)

data class StockSearch(
  val symbol : String,
  val name : String,
  val exchange : String,
  val assetType : String,
)

data class CompanyOverview(
  val `200DayMovingAverage`: String,
  val `50DayMovingAverage`: String,
  @SerializedName("52WeekHigh") val week52High : String,
  @SerializedName("52WeekLow") val week52Low : String,
  val Address: String,
  val AnalystTargetPrice: String,
  val AssetType: String,
  val Beta: String,
  val BookValue: String,
  val CIK: String,
  val Country: String,
  val Currency: String,
  val Description: String,
  val DilutedEPSTTM: String,
  val DividendDate: String,
  val DividendPerShare: String,
  val DividendYield: String,
  val EBITDA: String,
  val EPS: String,
  val EVToEBITDA: String,
  val EVToRevenue: String,
  val ExDividendDate: String,
  val Exchange: String,
  val FiscalYearEnd: String,
  val ForwardPE: String,
  val GrossProfitTTM: String,
  val Industry: String,
  val LatestQuarter: String,
  val MarketCapitalization: String,
  val Name: String,
  val OperatingMarginTTM: String,
  val PEGRatio: String,
  val PERatio: String,
  val PriceToBookRatio: String,
  val PriceToSalesRatioTTM: String,
  val ProfitMargin: String,
  val QuarterlyEarningsGrowthYOY: String,
  val QuarterlyRevenueGrowthYOY: String,
  val ReturnOnAssetsTTM: String,
  val ReturnOnEquityTTM: String,
  val RevenuePerShareTTM: String,
  val RevenueTTM: String,
  val Sector: String,
  val SharesOutstanding: String,
  val Symbol: String,
  val TrailingPE: String
)

data class StockPriceMetaData(
  @SerializedName("1. Information") val information : String,
  @SerializedName("2. Symbol") val symbol : String,
  @SerializedName("3. Last Refreshed") val lastRefreshed : String,
  @SerializedName("4. Interval") val interval : String,
  @SerializedName("5. Output Size") val outputSize : String,
  @SerializedName("6. Time Zone") val timeZone : String,
)

data class StockPrice(
  @SerializedName("1. open") val openPrice : String,
  @SerializedName("2. high") val highPrice : String,
  @SerializedName("3. low") val lowPrice : String,
  @SerializedName("4. close") val closePrice : String,
  @SerializedName("5. volume") val volume : String,
)

sealed class ApiStockPriceResponse

data class StockPriceResponse(
  @SerializedName("Meta Data") val metaData : StockPriceMetaData,
  @SerializedName("Time Series (5min)") val timeSeries : Map<String, StockPrice>
) : ApiStockPriceResponse()

data class StockPriceWeeklyResponse(
  @SerializedName("Meta Data") val metaData : StockPriceMetaData,
  @SerializedName("Time Series (60min)") val timeSeries : Map<String, StockPrice>
) : ApiStockPriceResponse()

data class StockPriceMonthlyMetaData(
  @SerializedName("1. Information") val information : String,
  @SerializedName("2. Symbol") val symbol : String,
  @SerializedName("3. Last Refreshed") val lastRefreshed : String,
  @SerializedName("4. Output Size") val outputSize : String,
  @SerializedName("5. Time Zone") val timeZone : String,
)

data class StockPriceYearlyMetaData(
  @SerializedName("1. Information") val information : String,
  @SerializedName("2. Symbol") val symbol : String,
  @SerializedName("3. Last Refreshed") val lastRefreshed: String,
  @SerializedName("4. Time Zone") val timeZone: String,
)

data class StockMonthlyPriceResponse(
  @SerializedName("Meta Data") val metaData : StockPriceMonthlyMetaData,
  @SerializedName("Time Series (Daily)") val timeSeries : Map<String, StockPrice>
) : ApiStockPriceResponse()

data class StockYearlyPriceResponse(
  @SerializedName("Meta Data") val metaData : StockPriceYearlyMetaData,
  @SerializedName("Weekly Time Series") val timeSeries: Map<String, StockPrice>
) : ApiStockPriceResponse()

data class StockPriceItem(
  val date : String,
  val price : String,
)


