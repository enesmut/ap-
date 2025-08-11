package com.seninadiniz.apiogren.network
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query
interface GoogleNewsApi {
    // Üst haberler (Türkiye / Türkçe)
    @GET("rss")
    suspend fun topHeadlines(
        @Query("hl") hl: String = "tr",
        @Query("gl") gl: String = "TR",
        @Query("ceid") ceid: String = "TR:tr"
    ): ResponseBody

    // Arama (ör: q=android)
    @GET("rss/search")
    suspend fun search(
        @Query("q") query: String,
        @Query("hl") hl: String = "tr",
        @Query("gl") gl: String = "TR",
        @Query("ceid") ceid: String = "TR:tr"
    ): ResponseBody
}