package com.example.newsapp

import retrofit2.Call
import retrofit2.http.GET

interface NewsCallable {

    @GET("/v2/top-headlines?country=us&category=general&apiKey=f5bc495de1db4feb91e8414f84ba2cd1&pageSize=30")
    fun getNews(): Call<News>
}