package com.example.newsapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.databinding.ActivityFavouritesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FavouritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavouritesBinding
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var adapter: NewsAdapter
    private val favArticles = ArrayList<Article>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavouritesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        adapter = NewsAdapter(this, favArticles)
        binding.recycleRView.layoutManager = LinearLayoutManager(this)
        binding.recycleRView.adapter = adapter

        loadFavourites()
    }

    private fun loadFavourites() {
        val userId = auth.currentUser?.uid ?: return
        val favRef = db.collection("users").document(userId).collection("favorites")

        favRef.get()
            .addOnSuccessListener { result ->
                favArticles.clear()
                for (doc in result) {
                    val article = doc.toObject(Article::class.java)
                    favArticles.add(article)
                }

                if (favArticles.isEmpty()) {
                    Toast.makeText(this, "No favorites found ❤️", Toast.LENGTH_SHORT).show()
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load favorites", Toast.LENGTH_SHORT).show()
            }
    }

    fun confirmAndRemoveFavorite(article: Article, position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Remove Favorite")
            .setMessage("Do you want to remove this article from your favorites?")
            .setPositiveButton("Yes") { _, _ ->
                val userId = auth.currentUser?.uid ?: return@setPositiveButton
                val favRef = db.collection("users").document(userId).collection("favorites")

                favRef.whereEqualTo("url", article.url).get()
                    .addOnSuccessListener { docs ->
                        for (doc in docs) {
                            favRef.document(doc.id).delete()
                        }
                        favArticles.removeAt(position)
                        adapter.notifyItemRemoved(position)
                        adapter.notifyItemRangeChanged(position, favArticles.size)
                        Toast.makeText(this, "Removed from favorites 💔", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
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
                Toast.makeText(this, "You are already in Favourites Page", Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}