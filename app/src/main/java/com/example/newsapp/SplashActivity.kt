package com.example.newsapp

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.addListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.newsapp.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val binding = ActivitySplashBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val logo = binding.image
        val btn = binding.startBtn
        val text = binding.text

        // --- Step 1: Logo animation ---
        logo.scaleX = 0.8f
        logo.scaleY = 0.8f

        // Scale up first (grow slowly)
        val scaleX = ObjectAnimator.ofFloat(logo, "scaleX", 0.8f, 2f)
        val scaleY = ObjectAnimator.ofFloat(logo, "scaleY", 0.8f, 2f)
        scaleX.duration = 1500
        scaleY.duration = 1500

        // Then move up
        val moveUp = ObjectAnimator.ofFloat(logo, "translationY", 0f, -700f)
        moveUp.duration = 1200
        moveUp.interpolator = DecelerateInterpolator()

        // Combine: grow first, then move up
        val logoAnim = AnimatorSet()
        logoAnim.play(scaleX).with(scaleY).before(moveUp)
        logoAnim.start()

        // --- Step 2: Show button & text after logo finishes ---
        logoAnim.addListener(onEnd = {
            btn.apply {
                alpha = 0f
                visibility = View.VISIBLE
                animate()
                    .alpha(1f)
                    .setDuration(1000)
                    .start()
            }
            text.apply {
                alpha = 0f
                visibility = View.VISIBLE
                animate()
                    .alpha(1f)
                    .setDuration(1000)
                    .start()
            }
        })
        btn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }


    }
}