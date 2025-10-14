package com.example.newsapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.newsapp.databinding.ArticleModelBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NewsAdapter(
    private val activity: Activity,
    private val articles: ArrayList<Article>
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val prefs = activity.getSharedPreferences("FavoritesPrefs", Context.MODE_PRIVATE)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val bind = ArticleModelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(bind)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = articles[position]
        val userId = auth.currentUser?.uid ?: "guest"

        val favoritesSet = prefs.getStringSet("favorites", emptySet()) ?: emptySet()
        val isFavorite = favoritesSet.contains(article.url)

        holder.binding.articleTitle.text = article.title
        Glide.with(holder.binding.articleImage.context)
            .load(article.urlToImage)
            .error(R.drawable.broken_image)
            .transition(DrawableTransitionOptions.withCrossFade(1000))
            .into(holder.binding.articleImage)

        holder.binding.favouriteButton.setImageResource(
            if (isFavorite) R.drawable.filled_favorite else R.drawable.favorite_border
        )

        holder.binding.articleCard.setOnClickListener { 
            activity.startActivity(Intent(Intent.ACTION_VIEW, article.url.toUri()))
        }

        holder.binding.shareButton.setOnClickListener {
            ShareCompat.IntentBuilder(activity)
                .setType("text/plain")
                .setChooserTitle("Share Article With:")
                .setText(article.url)
                .startChooser()
        }

        holder.binding.favouriteButton.setOnClickListener {
            val mutableFavorites = favoritesSet.toMutableSet()
            val articleUrl = article.url ?: return@setOnClickListener

            if (!isFavorite) {
                mutableFavorites.add(articleUrl)
                prefs.edit().putStringSet("favorites", mutableFavorites).apply()

                db.collection("users").document(userId)
                    .collection("favorites")
                    .whereEqualTo("url", articleUrl)
                    .get()
                    .addOnSuccessListener { docs ->
                        if (docs.isEmpty) {
                            db.collection("users").document(userId)
                                .collection("favorites").add(article)
                        }
                    }

                holder.binding.favouriteButton.setImageResource(R.drawable.filled_favorite)
                Toast.makeText(activity, "Added to favorites ❤️", Toast.LENGTH_SHORT).show()
            } else {
                mutableFavorites.remove(articleUrl)
                prefs.edit().putStringSet("favorites", mutableFavorites).apply()

                db.collection("users").document(userId)
                    .collection("favorites")
                    .whereEqualTo("url", articleUrl)
                    .get()
                    .addOnSuccessListener { docs ->
                        for (doc in docs) {
                            db.collection("users").document(userId)
                                .collection("favorites").document(doc.id).delete()
                        }
                    }

                holder.binding.favouriteButton.setImageResource(R.drawable.favorite_border)
                Toast.makeText(activity, "Removed from favorites 💔", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount() = articles.size

    class NewsViewHolder(val binding: ArticleModelBinding) : RecyclerView.ViewHolder(binding.root)
}
