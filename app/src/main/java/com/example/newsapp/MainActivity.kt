package com.example.newsapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.newsapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {


    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadNews()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.swipeRefresh.setOnRefreshListener { loadNews() }
    }

    fun loadNews() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://newsapi.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val c = retrofit.create(NewsCallable::class.java)
        c.getNews().enqueue(object : Callback<News> {
            override fun onResponse(
                call: Call<News?>,
                response: Response<News?>
            ) {
                val newsResponse = response.body()
                val articles = newsResponse?.articles!!
                articles.removeAll {
                    it.title == "[Removed]"
                }
                showData(articles)
                binding.progressBar.isVisible = false
                binding.swipeRefresh.isRefreshing = false
            }

            override fun onFailure(
                call: Call<News?>,
                t: Throwable
            ) {
                binding.progressBar.isVisible = false
                binding.swipeRefresh.isRefreshing = false

            }
        })
    }
    fun showData(articles: ArrayList<Article>){
        binding.recyclerView.adapter = NewsAdapter(this, articles)
    }

}