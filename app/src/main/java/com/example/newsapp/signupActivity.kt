package com.example.newsapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.newsapp.CategoryActivity
import com.example.newsapp.R
import com.example.newsapp.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth


class signUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySignupBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.nextBtn.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passEt.text.toString()
            if (binding.nameEt.text.isEmpty()) binding.nameLy.error = "Enter your name"
            else if (email.isEmpty()) binding.emailLy.error = "Enter your email"
            else if (pass.isEmpty()) binding.passLy.error = "Enter your password"
            else if (binding.confirmPassEt.text.toString() != pass) binding.passLy.error =
                "Passwords should match"
            else {
                val auth = FirebaseAuth.getInstance()
                auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser //بخزن البيانات

                        //هبعت ايميل ال verfiey لو اليوزر بيناته مبعوته ومش مبعوته null وهيطلعله رساله انه اتبعت ويرجعه ل صفحه ال login
                        user?.sendEmailVerification()?.addOnCompleteListener { verifyTask ->
                            if (verifyTask.isSuccessful) {
                                Toast.makeText(
                                    this, "Verification email sent to $email", Toast.LENGTH_LONG
                                ).show()
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                            }
                        }

                    } else { //لو حصل اي مشكله هيطلعلو رساله بيها
                        Toast.makeText(
                            this, "Signup failed: ${task.exception?.message}", Toast.LENGTH_LONG
                        ).show()
                    }
                }

            }
        }


    }


}