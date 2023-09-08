package com.example.myapplication.data.csv

import com.example.myapplication.data.api.StockSearch
import com.opencsv.CSVReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader

class StockListingsParser : CSVParser<StockSearch> {

  override suspend fun parse(stream: InputStream): List<StockSearch> {
    val csvReader = CSVReader(InputStreamReader(stream))
    return withContext(Dispatchers.IO) {
      csvReader
        .readAll()
        .drop(1)
        .mapNotNull { line ->
          val symbol = line.getOrNull(0)
          val name = line.getOrNull(1)
          val exchange = line.getOrNull(2)
          val assetType = line.getOrNull(3)
          StockSearch(
            name = name ?: return@mapNotNull null,
            symbol = symbol ?: return@mapNotNull null,
            exchange = exchange ?: return@mapNotNull null,
            assetType = assetType ?: return@mapNotNull null,
          )
        }
        .also {
          csvReader.close()
        }
    }
  }
}