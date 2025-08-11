package com.seninadiniz.apiogren.data

import com.seninadiniz.apiogren.network.GoogleNewsApi
import com.seninadiniz.apiogren.util.RssParser

class NewsRepository(private val api: GoogleNewsApi) {

    suspend fun getTopHeadlines(): Result<List<NewsItem>> = try {
        val xml = api.topHeadlines().string()
        Result.success(RssParser.parseItems(xml))
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun search(query: String): Result<List<NewsItem>> = try {
        val xml = api.search(query).string()
        Result.success(RssParser.parseItems(xml))
    } catch (e: Exception) {
        Result.failure(e)
    }
}