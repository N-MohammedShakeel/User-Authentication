package com.example.socialmediaapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.socialmediaapp.databinding.ActivityMain2Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class MainActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityMain2Binding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.extra2.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btsignin.setOnClickListener {
            val email = binding.edemail.text.toString().trim()
            val password = binding.edpass.text.toString().trim()

            if (validateInput(email, password)) {
                signInUser(email, password)
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding.edemail.error = "Email is required"
            binding.edemail.requestFocus()
            return false
        }

        if (password.isEmpty()) {
            binding.edpass.error = "Password is required"
            binding.edpass.requestFocus()
            return false
        }

        return true
    }

    private fun signInUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val intent = Intent(this, MainActivity3::class.java)
                startActivity(intent)
                finish()
            } else {
                handleSignInError(task.exception)
            }
        }
    }

    private fun handleSignInError(exception: Exception?) {
        when (exception) {
            is FirebaseAuthInvalidCredentialsException -> {
                Toast.makeText(this, "Invalid email or password.", Toast.LENGTH_SHORT).show()
            }
            is FirebaseAuthUserCollisionException -> {
                Toast.makeText(this, "This account does not exist.", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Sign-in failed: ${exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
