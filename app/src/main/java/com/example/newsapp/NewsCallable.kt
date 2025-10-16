package com.example.newsapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsCallable {

    @GET("v2/top-headlines")
    fun getNews(
        @Query("country") country: String,
        @Query("category") category: String = "general",
        @Query("apiKey") apiKey: String = "f5bc495de1db4feb91e8414f84ba2cd1",
        @Query("pageSize") pageSize: Int = 30
    ): Call<News>
}

