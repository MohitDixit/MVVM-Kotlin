package com.kotlin.mvvm.di

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import com.kotlin.mvvm.BuildConfig
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import com.kotlin.mvvm.api.ApiInterface
import com.kotlin.mvvm.ui.MainActivityViewModelFactory
import com.kotlin.mvvm.util.Utils
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetworkModule(private val app: Application) {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(application: Application): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BASIC

        val cacheDir = File(application.cacheDir, UUID.randomUUID().toString())
        val cache = Cache(cacheDir, BuildConfig.cacheSize * BuildConfig.cacheUnit * BuildConfig.cacheUnit)

        return OkHttpClient.Builder()
            .cache(cache)
            .connectTimeout(BuildConfig.connTimeout, TimeUnit.SECONDS)
            .readTimeout(BuildConfig.rwTimeout, TimeUnit.SECONDS)
            .writeTimeout(BuildConfig.rwTimeout, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(utils: Utils, okHttpClient: OkHttpClient): ApiInterface {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(utils.buildGsonConverterFactory())
            .addCallAdapterFactory(utils.buildRxJavaCallAdapterFactory())
            .client(okHttpClient)
            .build().create(ApiInterface::class.java)
    }

    @Provides
    @Singleton
    fun provideApplication(): Application = app

    @Provides
    @Singleton
    fun provideMainActivityViewModelFactory(
        factory: MainActivityViewModelFactory
    ): ViewModelProvider.Factory = factory

}