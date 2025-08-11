package com.seninadiniz.apiogren.network
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit



object NetworkModule {

    private val logging = HttpLoggingInterceptor { msg ->
        Log.d("OkHttp", msg)
    }.apply { level = HttpLoggingInterceptor.Level.BODY }

    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://news.google.com/") // <-- Google Haberler RSS
        .client(okHttp)
        // DİKKAT: Burada converter eklemiyoruz; ResponseBody alıp kendimiz parse edeceğiz.
        .build()

    val newsApi: GoogleNewsApi = retrofit.create(GoogleNewsApi::class.java)
}