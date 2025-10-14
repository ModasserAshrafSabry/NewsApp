package com.example.newsapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.databinding.ActivityFavouritesBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class FavouritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavouritesBinding
    private val db = Firebase.firestore
    private lateinit var adapter: NewsAdapter
    private val favArticles = ArrayList<Article>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavouritesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = NewsAdapter(this, favArticles)
        binding.recycleRView.layoutManager = LinearLayoutManager(this)
        binding.recycleRView.adapter = adapter
        setSupportActionBar(binding.toolbar)

        loadFavourites()
    }

    private fun loadFavourites() {
        db.collection("NewsDB").get()
            .addOnSuccessListener { result ->
                favArticles.clear()
                for (doc in result) {
                    val article = doc.toObject(Article::class.java)
                    article.id = doc.id
                    favArticles.add(article)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load favourites", Toast.LENGTH_SHORT).show()
            }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settingsBtn -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.logoutBtn -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.favouriteBtn -> {
                Toast.makeText(this, "You are already in Favourites Page", Toast.LENGTH_SHORT)
                    .show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}