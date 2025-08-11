package com.seninadiniz.apiogren.data

import com.seninadiniz.apiogren.network.JsonPlaceholderApi

class PostsRepository(private val api: JsonPlaceholderApi) {

    suspend fun getPosts(): Result<List<post>> = try {
        Result.success(api.getPosts())
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getPost(id: Int): Result<post> = try {
        Result.success(api.getPost(id))
    } catch (e: Exception) {
        Result.failure(e)
    }
}