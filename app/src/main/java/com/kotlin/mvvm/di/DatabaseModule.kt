package com.kotlin.mvvm.di

import android.app.Application
import androidx.room.Room
import com.kotlin.mvvm.BuildConfig
import com.kotlin.mvvm.repository.AppDatabase
import com.kotlin.mvvm.repository.OrderDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideOrderDatabase(app: Application): AppDatabase = Room.databaseBuilder(
        app,
        AppDatabase::class.java, BuildConfig.DATABASE_NAME
    )
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideOrderDao(
        database: AppDatabase
    ): OrderDao = database.orderDao()

}