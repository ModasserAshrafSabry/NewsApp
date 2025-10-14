package com.example.newsapp

data class News(
    val articles : ArrayList<Article>
)

data class Article(
    var id : String = "",
    var title : String="",
    var url : String="",
    var urlToImage : String=""
)
