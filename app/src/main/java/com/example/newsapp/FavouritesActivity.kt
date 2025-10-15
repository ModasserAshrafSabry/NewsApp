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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.databinding.ActivityFavouritesBinding

import com.example.newsapp.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore


import com.google.firebase.firestore.FirebaseFirestore


class FavouritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavouritesBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val prefs by lazy { getSharedPreferences("FavoritesPrefs", Context.MODE_PRIVATE) }

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

        setSupportActionBar(binding.toolbar)
        loadFavArticles()
    }

    private fun loadFavArticles() {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Please log in to view favorites", Toast.LENGTH_SHORT).show()
            return
        }

        val userFavorites = db.collection("users").document(user.uid).collection("favorites")

        userFavorites.get()
            .addOnSuccessListener { querySnapshot ->
                val favArticles = ArrayList<Article>()
                val favoritesSet = mutableSetOf<String>()

                for (doc in querySnapshot) {
                    val article = doc.toObject(Article::class.java)
                    favArticles.add(article)
                    article.url?.let { favoritesSet.add(it) }
                }

                prefs.edit().putStringSet("favorites", favoritesSet).apply()

                if (favArticles.isNotEmpty()) {
                    binding.recycleRView.layoutManager = LinearLayoutManager(this)
                    binding.recycleRView.adapter = NewsAdapter(this, favArticles)
                    Toast.makeText(this, "Favorites loaded successfully ❤️", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "No favorites found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error loading favorites", Toast.LENGTH_SHORT).show()
            }
    }


    private fun showData(articles: ArrayList<Article>) {
        binding.recycleRView.layoutManager = LinearLayoutManager(this)
        binding.recycleRView.adapter = NewsAdapter(this, articles)
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
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

                startActivity(Intent(this, LoginActivity::class.java))

                return true
            }


            R.id.favouriteBtn -> {
                Toast.makeText(this, "You are already in Favorites", Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
