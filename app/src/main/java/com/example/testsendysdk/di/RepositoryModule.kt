package com.example.testsendysdk.di

import com.example.testsendysdk.data.RepositoryImpl
import com.example.testsendysdk.domain.Repository
import dagger.Module
import dagger.Provides

@Module
class RepositoryModule {
    @Provides
    fun provideDeviceRepository(): Repository {
        return RepositoryImpl()
    }
}