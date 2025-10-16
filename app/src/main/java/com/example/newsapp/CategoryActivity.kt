package com.example.newsapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.newsapp.databinding.ActivityCategoryBinding

class CategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.generalCard.setOnClickListener { openNews("general") }
        binding.businessCard.setOnClickListener { openNews("business") }
        binding.entertainmentCard.setOnClickListener { openNews("entertainment") }
        binding.healthCard.setOnClickListener { openNews("health") }
        binding.scienceCard.setOnClickListener { openNews("science") }
        binding.sportsCard.setOnClickListener { openNews("sports") }
        binding.technologyCard.setOnClickListener { openNews("technology") }
    }

    private fun openNews(category: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("category", category)
        startActivity(intent)
    }
}
