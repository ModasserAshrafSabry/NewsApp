package com.example.newsapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.newsapp.databinding.ActivityFavouritesBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class FavouritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavouritesBinding
    private val db = Firebase.firestore
    private val favNews = db.collection("NewsDB")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityFavouritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.tooLbar)
        loadFavArticles()
    }

    private fun loadFavArticles() {
        favNews.get()
            .addOnSuccessListener { queryDocumentSnapshots ->
                val favArticles = ArrayList<Article>()

                for (article in queryDocumentSnapshots) {
                    val favArticle = article.toObject(Article::class.java)
                    favArticles.add(favArticle)
                }

                if (favArticles.isNotEmpty()) {
                    saveToSharedPreferences(favArticles)
                    showData(favArticles)
                    Toast.makeText(this, "Data Loaded successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "No Favourites", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error loading data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showData(articles: ArrayList<Article>) {
        binding.recycleRView.adapter = NewsAdapter(this, articles)
    }


    private fun saveToSharedPreferences(favArticles: ArrayList<Article>) {
        val prefs = getSharedPreferences("FavoritesPrefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val favSet = favArticles.mapNotNull { it.url }.toSet()
        editor.putStringSet("favorites", favSet)
        editor.apply()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settingsBtn -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }
            R.id.logoutBtn -> {
                startActivity(Intent(this, LoginActivity::class.java))
                return true
            }
            R.id.favouriteBtn -> {
                Toast.makeText(this, "You are already in Favourites", Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
