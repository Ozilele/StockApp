package com.example.myapplication.data

import com.example.myapplication.data.api.ApiStockPriceResponse
import retrofit2.Response

// Interface with methods for making api calls
interface ApiRepository {
  suspend fun <T: ApiStockPriceResponse> getStockPrices(symbol: String, interval : FilterType) : Response<T>?
}