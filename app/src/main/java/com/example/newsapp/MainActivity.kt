package com.example.newsapp

import android.os.Bundle
import android.widget.Toast
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

    private fun loadNews() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://newsapi.org")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val c = retrofit.create(NewsCallable::class.java)

        val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val selectedCountry = prefs.getString("selectedCountry", "us") ?: "us"

        val selectedCategory = intent.getStringExtra("category") ?: "general"

        binding.progressBar.isVisible = true

        c.getNews(selectedCountry, selectedCategory).enqueue(object : Callback<News> {
            override fun onResponse(call: Call<News?>, response: Response<News?>) {
                binding.progressBar.isVisible = false
                binding.swipeRefresh.isRefreshing = false

                val newsResponse = response.body()
                val articles = newsResponse?.articles ?: arrayListOf()

                articles.removeAll { it.title == "[Removed]" }

                if (articles.isEmpty()) {
                    Toast.makeText(
                        this@MainActivity,
                        "⚠️ No news available for this country/category.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                showData(articles)
            }

            override fun onFailure(call: Call<News?>, t: Throwable) {
                binding.progressBar.isVisible = false
                binding.swipeRefresh.isRefreshing = false
                Toast.makeText(
                    this@MainActivity,
                    "❌ Failed to load news. Check your connection.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun showData(articles: ArrayList<Article>) {
        binding.recyclerView.adapter = NewsAdapter(this, articles)
    }
}
