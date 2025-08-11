package com.seninadiniz.apiogren.network
import  com.seninadiniz.apiogren.data.post
import retrofit2.http.GET
import retrofit2.http.Path

interface JsonPlaceholderApi {
    @GET("posts")
    suspend fun getPosts(): List<post>

    @GET("posts/{id}")
    suspend fun getPost(@Path("id") id: Int): post
}