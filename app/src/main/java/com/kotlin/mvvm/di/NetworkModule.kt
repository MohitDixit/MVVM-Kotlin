package com.kotlin.mvvm.di

import android.content.Context
import com.kotlin.mvvm.BuildConfig
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.kotlin.mvvm.R
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import com.kotlin.mvvm.api.ApiInterface
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(context: Context): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BASIC

        val cacheDir = File(context.cacheDir, UUID.randomUUID().toString())
        val cache = Cache(cacheDir, context.resources.getInteger(R.integer.cacheSize).toLong() * context.resources.getInteger(R.integer.cacheUnit).toLong() * context.resources.getInteger(R.integer.cacheUnit).toLong())

        val okHttpClient = OkHttpClient.Builder()
            .cache(cache)
            .connectTimeout(context.resources.getInteger(R.integer.connTimeout).toLong(), TimeUnit.SECONDS)
            .readTimeout(context.resources.getInteger(R.integer.rwTimeout).toLong(), TimeUnit.SECONDS)
            .writeTimeout(context.resources.getInteger(R.integer.rwTimeout).toLong(), TimeUnit.SECONDS)

        return if(BuildConfig.DEBUG) okHttpClient.addInterceptor(interceptor).build()
        else okHttpClient.build()
    }

    @Provides
    @Singleton
    fun provideApiService(
        gsonConverterFactory: GsonConverterFactory,
        rxJava2CallAdapterFactory: RxJava2CallAdapterFactory,
        okHttpClient: OkHttpClient
    ): ApiInterface {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(gsonConverterFactory)
            .addCallAdapterFactory(rxJava2CallAdapterFactory)
            .client(okHttpClient)
            .build().create(ApiInterface::class.java)
    }

    @Provides
    @Singleton
    fun buildGsonConverterFactory(gson: Gson): GsonConverterFactory {
        return GsonConverterFactory.create(gson)
    }

    @Provides
    @Singleton
    fun buildRxJavaCallAdapterFactory(): RxJava2CallAdapterFactory {
        return RxJava2CallAdapterFactory.create()
    }


}