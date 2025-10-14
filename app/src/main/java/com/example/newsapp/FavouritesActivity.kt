package com.example.newsapp

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
import com.example.newsapp.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore


class FavouritesActivity : AppCompatActivity() {
    lateinit var binding: ActivityFavouritesBinding
    val db = Firebase.firestore
    val favNews = db.collection("NewsDB")


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


    fun loadFavArticles() {
        favNews.get().addOnSuccessListener {
            queryDocumentSnapshots ->
            val favArticles = ArrayList<Article>()

            for(article in queryDocumentSnapshots){
                val favArticle = article.toObject(Article::class.java)
                favArticles.add(favArticle)
            }
           if(favArticles.isNotEmpty()){
               showData(favArticles)
               Toast.makeText(this
                   , "Data Loaded successfully", Toast.LENGTH_SHORT).show()

           }
            else {
               Toast.makeText(this, "NO FAVOURITES", Toast.LENGTH_SHORT).show()}
            }
            .addOnFailureListener { Toast.makeText(this
                , "Error", Toast.LENGTH_SHORT).show() }

    }

    fun showData(articles: ArrayList<Article>) {
        binding.recycleRView.adapter = NewsAdapter(this, articles)
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
                Toast.makeText(this, "You are already in Favourites", Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}