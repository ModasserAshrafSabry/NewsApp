package com.example.newsapp

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var btnEgypt: Button
    private lateinit var btnUSA: Button
    private lateinit var btnUK: Button
    private lateinit var btnSave: Button
    private lateinit var prefs: SharedPreferences
    private var selectedCountry: String = "us"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        btnEgypt = findViewById(R.id.btnEgypt)
        btnUSA = findViewById(R.id.btnUSA)
        btnUK = findViewById(R.id.btnUK)
        btnSave = findViewById(R.id.btnSave)
        prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        selectedCountry = prefs.getString("selectedCountry", "us") ?: "us"
        highlightSelectedButton()

        btnEgypt.setOnClickListener {
            selectedCountry = "eg"
            highlightSelectedButton()
        }

        btnUSA.setOnClickListener {
            selectedCountry = "us"
            highlightSelectedButton()
        }

        btnUK.setOnClickListener {
            selectedCountry = "gb"
            highlightSelectedButton()
        }

        btnSave.setOnClickListener {
            val editor = prefs.edit()
            editor.putString("selectedCountry", selectedCountry)
            editor.apply()

            Toast.makeText(this, "✅ Country saved successfully!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun highlightSelectedButton() {
        val defaultColor = Color.parseColor("#1976D2")
        val selectedColor = Color.parseColor("#64B5F6")

        btnEgypt.background.setTint(if (selectedCountry == "eg") selectedColor else defaultColor)
        btnUSA.background.setTint(if (selectedCountry == "us") selectedColor else defaultColor)
        btnUK.background.setTint(if (selectedCountry == "gb") selectedColor else defaultColor)
    }
}
