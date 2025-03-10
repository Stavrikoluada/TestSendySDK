package com.example.testsendysdk.di

import com.example.testsendysdk.data.remote.WalletApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://testwallet.sendy.land/"

@Module
class RemoteModule {

    @Provides
    fun provideBookApi(): WalletApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WalletApi::class.java)
    }
}