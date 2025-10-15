package com.example.newsapp

import android.app.Activity
import android.app.AlertDialog
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

class NewsAdapter(val activity: Activity, val articles: ArrayList<Article>) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val bind = ArticleModelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(bind)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = articles[position]
        val userId = auth.currentUser?.uid ?: "guest"
        val articleUrl = article.url ?: return
        val safeId = URLEncoder.encode(articleUrl, "UTF-8")

        holder.binding.articleTitle.text = article.title

        Glide.with(holder.binding.articleImage.context)
            .load(article.urlToImage)
            .error(R.drawable.broken_image)
            .transition(DrawableTransitionOptions.withCrossFade(1000))
            .into(holder.binding.articleImage)

        // Open article on click
        holder.binding.articleCard.setOnClickListener {
            activity.startActivity(Intent(Intent.ACTION_VIEW, articleUrl.toUri()))
        }

        // Share article
        holder.binding.shareButton.setOnClickListener {
            ShareCompat.IntentBuilder(activity)
                .setType("text/plain")
                .setChooserTitle("Share Article With:")
                .setText(articleUrl)
                .startChooser()
        }

        // Handle favourites
        holder.binding.favouriteButton.setOnClickListener {
            if (activity is MainActivity) {
                db.collection("users").document(userId)
                    .collection("favorites").document(safeId)
                    .get()
                    .addOnSuccessListener { doc ->
                        if (doc.exists()) {
                            // Remove from favorites
                            db.collection("users").document(userId)
                                .collection("favorites").document(safeId)
                                .delete()
                                .addOnSuccessListener {
                                    holder.binding.favouriteButton.setImageResource(R.drawable.favorite_border)
                                    Toast.makeText(activity, "Removed from favorites 💔", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            // Add to favorites
                            db.collection("users").document(userId)
                                .collection("favorites").document(safeId)
                                .set(article)
                                .addOnSuccessListener {
                                    holder.binding.favouriteButton.setImageResource(R.drawable.filled_favorite)
                                    Toast.makeText(activity, "Added to favorites ❤️", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }

            } else if (activity is FavouritesActivity) {
                val builder = AlertDialog.Builder(activity)
                    .setTitle("Alert")
                    .setMessage("Remove this article from favourites?")
                    .setPositiveButton("Yes") { _, _ ->
                        db.collection("users").document(userId)
                            .collection("favorites").document(safeId)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(activity, "Removed successfully!", Toast.LENGTH_SHORT).show()
                                articles.removeAt(position)
                                notifyItemRemoved(position)
                                notifyItemRangeChanged(position, articles.size)
                            }
                            .addOnFailureListener {
                                Toast.makeText(activity, "Error while Removing", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                builder.create().show()
            }
        }
    }

    override fun getItemCount() = articles.size

    class NewsViewHolder(val binding: ArticleModelBinding) :
        RecyclerView.ViewHolder(binding.root)
}
