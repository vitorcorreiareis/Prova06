package com.example.prova06.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val viaCepService: ViaCepService by lazy {
        Retrofit.Builder()
            .baseUrl("https://viacep.com.br/ws/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ViaCepService::class.java)
    }
}
