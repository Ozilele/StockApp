package com.example.myapplication

import com.example.myapplication.data.api.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// Api for making requests to alphavantage.com
interface StockApi {

  @GET("/query?function=SYMBOL_SEARCH")
  suspend fun getStockMatches(
    @Query("keywords") keywords : String,
    @Query("apikey") apiKey: String = "9XN0AU4DPYRCG274"
  ) : Response<StockMatchResponse>

  @GET("/query?function=LISTING_STATUS")
  suspend fun getListings(
    @Query("apikey") apiKey: String = "9XN0AU4DPYRCG274"
  ) : ResponseBody

  @GET("/query?function=OVERVIEW")
  suspend fun getCompanyInfo(
    @Query("symbol") symbol : String,
    @Query("apikey") apiKey: String = "9XN0AU4DPYRCG274"
  ) : Response<CompanyOverview>

  @GET("/query?function=TIME_SERIES_INTRADAY")
  suspend fun getLatestDayStockPrices(
    @Query("symbol") symbol : String,
    @Query("interval") interval : String = "5min",
    @Query("adjusted") adjusted : Boolean = true,
    @Query("apikey") apiKey: String = "9XN0AU4DPYRCG274",
    @Query("outputsize") outputSize : String = "compact",
  ) : Response<StockPriceResponse>

  @GET("/query?function=TIME_SERIES_INTRADAY")
  suspend fun getLatestWeeklyStockPrices(
    @Query("symbol") symbol : String,
    @Query("interval") interval : String = "60min",
    @Query("adjusted") adjusted : Boolean = true,
    @Query("apikey") apiKey: String = "9XN0AU4DPYRCG274",
    @Query("outputsize") outputSize : String = "compact",
  ) : Response<StockPriceWeeklyResponse>

  @GET("/query?function=TIME_SERIES_DAILY")
  suspend fun getMonthlyStockPrices(
    @Query("symbol") symbol : String,
    @Query("outputsize") outputsize : String = "compact",
    @Query("apikey") apiKey: String = "9XN0AU4DPYRCG274",
  ) : Response<StockMonthlyPriceResponse>

  @GET("/query?function=TIME_SERIES_WEEKLY")
  suspend fun getYearlyStockPrices(
    @Query("symbol") symbol : String,
    @Query("apikey") apiKey: String = "9XN0AU4DPYRCG274",
  ) : Response<StockYearlyPriceResponse>

}