package com.example.myapplication.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.myapplication.AuthApi
import com.example.myapplication.StockApi
import com.example.myapplication.data.ApiRepository
import com.example.myapplication.data.ApiRepositoryImpl
import com.example.myapplication.data.backend.auth.AuthRepository
import com.example.myapplication.data.backend.auth.AuthRepositoryImpl
import com.example.myapplication.data.db.StockDatabase
import com.example.myapplication.viewModel.AuthViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // lives as long as the application does
object AppModule {

  @Singleton // single instance in the application lifecycle
  @Provides
  fun provideStockApiInstance() : StockApi {
    return Retrofit.Builder()
      .baseUrl("https://www.alphavantage.co")
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(StockApi::class.java)
  }

  @Singleton
  @Provides
  fun provideAuthApiInstance() : AuthApi { // authApi instance
    return Retrofit.Builder()
      .baseUrl(AuthApi.BASE_URL)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(AuthApi::class.java)
  }

  @Singleton
  @Provides
  fun provideStockDatabase(
    app : Application
  ) : StockDatabase {
    return Room
      .databaseBuilder(app, StockDatabase::class.java, "stock-DB")
      .build()
  }

  @Singleton
  @Provides
  fun provideApiRepository(
    retrofitInstance : StockApi
  ) : ApiRepository {
    return ApiRepositoryImpl(retrofitInstance)
  }

  @Singleton
  @Provides
  @Named("Preferences Auth")
  fun provideSharedPreferences(
    app: Application
  ): SharedPreferences {
//    val preferences = app.getSharedPreferences("prefs", Context.MODE_PRIVATE)
//    preferences.edit().clear().apply()
    return app.getSharedPreferences("prefs", Context.MODE_PRIVATE)
  }

  @Singleton
  @Provides
  @Named("Preferences Mode")
  fun provideSharedPreferencesMode(
    app: Application
  ) : SharedPreferences {
    return app.getSharedPreferences("MODE", Context.MODE_PRIVATE)
  }

  @Singleton
  @Provides
  fun provideAuthRepository(api: AuthApi, @Named("Preferences Auth") prefs: SharedPreferences): AuthRepository {
    return AuthRepositoryImpl(api, prefs)
  }

  @Singleton
  @Provides
  fun provideAuthViewModel(repository: AuthRepository): AuthViewModel {
    return AuthViewModel(repository)
  }


}