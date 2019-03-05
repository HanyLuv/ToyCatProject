package com.example.mycatapp

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

open class Repository(
    baseUrl: String,
    isDebugEnabled: Boolean,
    apiKey: String
) {
    private val apiKeyHeader: String = "x-api-key"
    protected val retrofit: Retrofit

    init {
        val loggingInterceptor = HttpLoggingInterceptor()

        loggingInterceptor.level =  if (isDebugEnabled) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE

//        val client =OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor())
        val client = OkHttpClient.Builder().addInterceptor { chain: Interceptor.Chain ->
            val request = chain.request().newBuilder()
                .addHeader(apiKeyHeader, apiKey)
                .build()
            chain.proceed(request)
        }.addInterceptor(loggingInterceptor).build()

        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    }
}