package com.example.newsapp


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.newsapp.databinding.ActivityLoginBinding
import com.google.firebase.Firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

lateinit var auth: FirebaseAuth

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//instance firebase
        auth = FirebaseAuth.getInstance()




        binding.singnupTxt.setOnClickListener {
            startActivity(Intent(this, signUpActivity::class.java))
        }


        binding.logBtn.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passEt.text.toString()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
            //هنا بنبعت ل فاير بيز الاميل والباسورد وبنشوف هل العمليه نجحت ولا لا
            auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser  //بنخزن بيانات المستخدم
                    if (user != null && user.isEmailVerified) {
                        startActivity(Intent(this, CategoryActivity::class.java))
                        finish()  //لو اليوزر عامل تاكيد للايميل والبيانات بتاعته اتبعتت فعلا هيخش علي ال category
                    } else { //لو مش عامل تاكيد هبعتله ياكد
                        Toast.makeText(this, "Please verify your email first", Toast.LENGTH_LONG)
                            .show()
                        auth.signOut()
                    }
                } else { // لو حصل اي مشكله بنبعتله رساله بالمشكله اللي حصلت
                    Toast.makeText(
                        this,
                        "Login failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        }

        binding.forgetBtn.setOnClickListener {
            val email = binding.emailEt.text.toString()
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Firebase.auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "email sent", Toast.LENGTH_SHORT).show()

                    }
                }
        }




    }
}




