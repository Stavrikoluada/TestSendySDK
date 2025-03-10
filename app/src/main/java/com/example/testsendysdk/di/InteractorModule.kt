package com.example.testsendysdk.di

import com.example.testsendysdk.domain.Repository
import com.example.testsendysdk.domain.interactor.Interactor
import com.example.testsendysdk.domain.interactor.InteractorImpl
import dagger.Module
import dagger.Provides

@Module
class InteractorModule {
    @Provides
    fun provideDeviceInteractor(repository: Repository): Interactor {
        return InteractorImpl(repository)
    }
}