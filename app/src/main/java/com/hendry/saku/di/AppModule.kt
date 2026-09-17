package com.hendry.saku.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppName(): String {
        return "Saku App"
    }

    @Provides
    @Singleton
    fun provideConnectivityObserver(@dagger.hilt.android.qualifiers.ApplicationContext context: android.content.Context): com.hendry.saku.utils.network.ConnectivityObserver {
        return com.hendry.saku.utils.network.NetworkConnectivityObserver(context)
    }
}