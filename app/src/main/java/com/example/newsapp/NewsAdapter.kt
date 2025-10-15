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
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore


class NewsAdapter(val activity: Activity, val articles: ArrayList<Article>) :
    RecyclerView.Adapter<NewsAdapter
    .NewsViewHolder>() {

    val db = Firebase.firestore
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewsViewHolder {


        val bind = ArticleModelBinding
            .inflate(
                LayoutInflater
                    .from(parent.context), parent, false
            )
        return NewsViewHolder(bind)
    }

    override fun onBindViewHolder(
        holder: NewsViewHolder,
        position: Int
    ) {
        holder.binding.articleTitle.text = articles[position].title
        Glide
            .with(holder.binding.articleImage.context)
            .load(articles[position].urlToImage)
            .error(R.drawable.broken_image)
            .transition(DrawableTransitionOptions.withCrossFade(1000))
            .into(holder.binding.articleImage)

        holder.binding.articleCard.setOnClickListener {
            activity.startActivity(
                Intent(Intent.ACTION_VIEW, articles[position].url.toUri())
            )
        }

        holder.binding.shareButton.setOnClickListener {
            ShareCompat
                .IntentBuilder(activity)
                .setType("text/plain")
                .setChooserTitle("Share Article With: ")
                .setText(articles[position].url)
                .startChooser()
        }

        holder.binding.favouriteButton.tag = R.drawable.favorite_border

        holder.binding.favouriteButton.setOnClickListener {
            if (activity is MainActivity) {
                if (holder.binding.favouriteButton.tag == R.drawable.favorite_border) {

                    db.collection("NewsDB").add(articles[position])
                        .addOnSuccessListener { documentReference ->
                            articles[position].id = documentReference.id

                            documentReference.update("id", articles[position].id)

                            holder.binding.favouriteButton.setImageResource(R.drawable.filled_favorite)
                            holder.binding.favouriteButton.tag = R.drawable.filled_favorite
                            Toast.makeText(this.activity, "liked!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show()
                        }

                }else {
                    holder.binding.favouriteButton.setImageResource(R.drawable.favorite_border)
                    holder.binding.favouriteButton.tag = R.drawable.favorite_border
                }

                }   else if (activity is FavouritesActivity) {
            val builder = AlertDialog.Builder(activity)
                .setTitle("Alert")
                .setMessage("Remove this article from favourites?")
                .setPositiveButton("Yes") { _, _ ->
                    db.collection("NewsDB").document(articles[position].id).delete()
                        .addOnSuccessListener {
                            Toast.makeText(
                                activity,
                                "Removed successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                            articles.removeAt(position)
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position, articles.size)
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                activity,
                                "Error while Removing",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
            builder.create().show()
                }
            }

        }

    override fun getItemCount() = articles.size
    class NewsViewHolder(val binding: ArticleModelBinding) : RecyclerView.ViewHolder(binding.root)
}