package com.example.newsapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.newsapp.databinding.ArticleModelBinding

class NewsAdapter(val activity: Activity, val articles: ArrayList<Article>) :
    RecyclerView.Adapter<NewsAdapter
    .NewsViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewsViewHolder {
        val bind = ArticleModelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

        /////
        holder.binding.favouriteButton.tag = R.drawable.favorite_border

        holder.binding.favouriteButton.setOnClickListener {
            if (holder.binding.favouriteButton.tag == R.drawable.favorite_border) {

                holder.binding.favouriteButton.setImageResource(R.drawable.filled_favorite)
                holder.binding.favouriteButton.tag = R.drawable.filled_favorite
                Toast.makeText(activity, "Liked!", Toast.LENGTH_SHORT).show()

            } else {
                holder.binding.favouriteButton.setImageResource(R.drawable.favorite_border)
                holder.binding.favouriteButton.tag = R.drawable.favorite_border
                Toast.makeText(activity, "UnLiked", Toast.LENGTH_SHORT).show()


            }

        }
        ////////
    }

    override fun getItemCount() = articles.size
    class NewsViewHolder(val binding: ArticleModelBinding) : RecyclerView.ViewHolder(binding.root)
}