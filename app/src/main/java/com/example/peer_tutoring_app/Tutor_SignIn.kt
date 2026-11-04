package com.example.peer_tutoring_app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Tutor_SignIn : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tutor_sign_in)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        val loginBtn = findViewById<Button>(R.id.tutor_button_Login)
        val emailEditText = findViewById<EditText>(R.id.Tutor_Email)
        val passwordEditText = findViewById<EditText>(R.id.Tutor_Password)

        loginBtn.setOnClickListener {
            val email = emailEditText.text.trim().toString()
            val password = passwordEditText.text.trim().toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginTutor(email, password)
            } else {
                Snackbar.make(loginBtn, "Please enter email and password", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun loginTutor(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val db = FirebaseFirestore.getInstance()
                    db.collection("Tutors").document(email).get()
                        .addOnSuccessListener { document ->
                            val intent = Intent(this, Tutor_Homepage::class.java)
                            if (document != null && document.exists()) {
                                val name = document.getString("Name")
                                intent.putExtra("USER_NAME", name)
                            }
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener {
                            startActivity(Intent(this, Tutor_Homepage::class.java))
                            finish()
                        }
                } else {
                    Snackbar.make(
                        findViewById(R.id.tutor_button_Login),
                        "Authentication failed: ${task.exception?.message}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
    }

    fun onclick_tutorsignin_back_to_main(view: View){
        startActivity(Intent(this, MainActivity::class.java))
    }

    fun tutorsignin_to_tutorsignup(view: View){
        startActivity(Intent(this, Tutor_Sign_Up::class.java))
    }
}