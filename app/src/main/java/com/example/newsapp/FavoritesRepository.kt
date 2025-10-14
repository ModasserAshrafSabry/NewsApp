package com.example.newsapp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await

object FavoritesRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val userId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: "guest"

    private fun favoritesCollection() =
        firestore.collection("users").document(userId).collection("favorites")

    suspend fun addFavorite(article: Article) {
        val articleId = article.url ?: return
        val docRef = favoritesCollection().document(articleId)
        val snapshot = docRef.get().await()
        if (!snapshot.exists()) {
            docRef.set(article).await()
        }
    }

    suspend fun removeFavorite(articleUrl: String) {
        favoritesCollection().document(articleUrl).delete().await()
    }

    suspend fun isFavorite(articleUrl: String): Boolean {
        val snapshot = favoritesCollection().document(articleUrl).get().await()
        return snapshot.exists()
    }

    suspend fun getAllFavorites(): List<Article> {
        val snapshot: QuerySnapshot = favoritesCollection().get().await()
        return snapshot.toObjects(Article::class.java)
    }
}

