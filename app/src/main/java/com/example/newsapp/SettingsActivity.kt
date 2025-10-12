package com.example.newsapp

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val countryGroup = findViewById<RadioGroup>(R.id.countryGroup)
        val saveButton = findViewById<Button>(R.id.btn_save_country)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val savedCountry = sharedPreferences.getString("selectedCountry", "")
        if (!savedCountry.isNullOrEmpty()) {
            when (savedCountry) {
                "eg" -> countryGroup.check(R.id.rb_egypt)
                "us" -> countryGroup.check(R.id.rb_usa)
                "gb" -> countryGroup.check(R.id.rb_uk)
            }
        }


        saveButton.setOnClickListener {
            val selectedId = countryGroup.checkedRadioButtonId
            val selectedCountry = when (selectedId) {
                R.id.rb_egypt -> "eg"
                R.id.rb_usa -> "us"
                R.id.rb_uk -> "gb"
                else -> ""
            }

            if (selectedCountry.isNotEmpty()) {
                editor.putString("selectedCountry", selectedCountry)
                editor.apply()
                Toast.makeText(this, "Country saved: $selectedCountry", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Please choose a country first!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
