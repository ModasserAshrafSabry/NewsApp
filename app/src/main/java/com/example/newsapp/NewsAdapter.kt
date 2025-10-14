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
import java.net.URLEncoder

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
        val articleUrl = article.url ?: return

        val safeId = URLEncoder.encode(articleUrl, "UTF-8")

        val isFavorite = prefs.getBoolean(safeId, false)

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
            activity.startActivity(Intent(Intent.ACTION_VIEW, articleUrl.toUri()))
        }

        holder.binding.shareButton.setOnClickListener {
            ShareCompat.IntentBuilder(activity)
                .setType("text/plain")
                .setChooserTitle("Share Article With:")
                .setText(articleUrl)
                .startChooser()
        }

        holder.binding.favouriteButton.setOnClickListener {
            val editor = prefs.edit()

            if (!isFavorite) {
                db.collection("users").document(userId)
                    .collection("favorites").document(safeId)
                    .set(article)
                    .addOnSuccessListener {
                        editor.putBoolean(safeId, true).apply()
                        holder.binding.favouriteButton.setImageResource(R.drawable.filled_favorite)
                        Toast.makeText(activity, "Added to favorites ❤️", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(activity, "Failed to add!", Toast.LENGTH_SHORT).show()
                    }

            } else {
                db.collection("users").document(userId)
                    .collection("favorites").document(safeId)
                    .delete()
                    .addOnSuccessListener {
                        editor.remove(safeId).apply()
                        holder.binding.favouriteButton.setImageResource(R.drawable.favorite_border)
                        Toast.makeText(activity, "Removed from favorites 💔", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(activity, "Failed to remove!", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    override fun getItemCount() = articles.size

    class NewsViewHolder(val binding: ArticleModelBinding) : RecyclerView.ViewHolder(binding.root)
}
