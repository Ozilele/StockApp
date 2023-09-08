package com.example.myapplication.data

import com.example.myapplication.StockApi
import com.example.myapplication.data.api.ApiStockPriceResponse
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

// A class which is responsible for making api calls about stock prices
class ApiRepositoryImpl(
  private val retrofitInstance : StockApi
) : ApiRepository {
  override suspend fun <T : ApiStockPriceResponse> getStockPrices(symbol: String, interval : FilterType): Response<T>? {
    var stockPricesResponse : Response<T>? = null
    try {
      stockPricesResponse = when(interval) {
        FilterType.DAILY -> retrofitInstance.getLatestDayStockPrices(symbol = symbol) as Response<T>
        FilterType.WEEKLY -> retrofitInstance.getLatestWeeklyStockPrices(symbol = symbol) as Response<T>
        FilterType.MONTHLY -> retrofitInstance.getMonthlyStockPrices(symbol = symbol) as Response<T>
        FilterType.MONTHLY_3 -> retrofitInstance.getMonthlyStockPrices(symbol = symbol) as Response<T>
        FilterType.YEARLY -> retrofitInstance.getYearlyStockPrices(symbol = symbol) as Response<T>
        else -> null
      }
    } catch(e : IOException) {
      e.printStackTrace()
    } catch(e: HttpException) {
      e.printStackTrace()
    }
    return stockPricesResponse
  }

}