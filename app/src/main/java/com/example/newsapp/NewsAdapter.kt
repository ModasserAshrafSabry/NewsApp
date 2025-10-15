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

class NewsAdapter(
    private val activity: Activity,
    private val articles: ArrayList<Article>
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ArticleModelBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = articles[position]

        holder.binding.articleTitle.text = article.title
        Glide.with(holder.binding.articleImage.context)
            .load(article.urlToImage)
            .error(R.drawable.broken_image)
            .transition(DrawableTransitionOptions.withCrossFade(1000))
            .into(holder.binding.articleImage)

        holder.binding.articleCard.setOnClickListener {
            activity.startActivity(Intent(Intent.ACTION_VIEW, article.url?.toUri()))
        }

        holder.binding.shareButton.setOnClickListener {
            ShareCompat.IntentBuilder(activity)
                .setType("text/plain")
                .setChooserTitle("Share Article With:")
                .setText(article.url)
                .startChooser()
        }

        holder.binding.favouriteButton.setImageResource(R.drawable.favorite_border)
        holder.binding.favouriteButton.tag = R.drawable.favorite_border

        holder.binding.favouriteButton.setOnClickListener {
            val user = auth.currentUser

            if (activity is MainActivity) {
                if (holder.binding.favouriteButton.tag == R.drawable.favorite_border) {
                    if (user != null) {
                        val userFavs = db.collection("users")
                            .document(user.uid)
                            .collection("favorites")

                        // Add article to user's favorites
                        userFavs.add(article)
                            .addOnSuccessListener { documentReference ->
                                article.id = documentReference.id
                                documentReference.update("id", article.id)

                                holder.binding.favouriteButton.setImageResource(R.drawable.filled_favorite)
                                holder.binding.favouriteButton.tag = R.drawable.filled_favorite
                                Toast.makeText(activity, "Added to favorites ❤️", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(activity, "Failed to add to favorites", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(activity, "Please log in to save favorites", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    holder.binding.favouriteButton.setImageResource(R.drawable.favorite_border)
                    holder.binding.favouriteButton.tag = R.drawable.favorite_border
                }

            } else if (activity is FavouritesActivity) {
                val builder = AlertDialog.Builder(activity)
                    .setTitle("Remove Favorite")
                    .setMessage("Remove this article from your favorites?")
                    .setPositiveButton("Yes") { _, _ ->
                        if (user != null) {
                            db.collection("users").document(user.uid)
                                .collection("favorites")
                                .document(article.id)
                                .delete()
                                .addOnSuccessListener {
                                    Toast.makeText(activity, "Removed successfully!", Toast.LENGTH_SHORT).show()
                                    articles.removeAt(position)
                                    notifyItemRemoved(position)
                                    notifyItemRangeChanged(position, articles.size)
                                }
                                .addOnFailureListener {
                                    Toast.makeText(activity, "Error while removing", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                    .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }

                builder.create().show()
            }
        }
    }

    override fun getItemCount(): Int = articles.size

    class NewsViewHolder(val binding: ArticleModelBinding) :
        RecyclerView.ViewHolder(binding.root)
}
