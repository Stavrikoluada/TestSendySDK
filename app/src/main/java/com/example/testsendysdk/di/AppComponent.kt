package com.example.testsendysdk.di

import com.example.testsendysdk.presentation.MainActivity
import dagger.Component
import jakarta.inject.Singleton

@Component(modules = [/*AppModule::class*/ RepositoryModule::class, InteractorModule::class, RemoteModule::class])
@Singleton
interface AppComponent {

    fun inject(mainActivity: MainActivity)
}