package com.example.socialmediaapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.socialmediaapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.extra1.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
            finish()
        }

        binding.btsignup.setOnClickListener {
            val name = binding.eduser.text.toString().trim()
            val password = binding.edpass.text.toString().trim()
            val email = binding.edemail.text.toString().trim()
            val about = binding.edabout.text.toString().trim()

            if (validateInput(name, email, password)) {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        sendEmailVerification()
                        saveUser(auth, name, email, about)

                        Toast.makeText(this, "Signup successful. Please verify your email.", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, MainActivity2::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        handleSignupError(task.exception)
                    }
                }
            }
        }
    }

    private fun validateInput(name: String, email: String, password: String): Boolean {
        if (name.isEmpty()) {
            binding.eduser.error = "Name is required"
            binding.eduser.requestFocus()
            return false
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edemail.error = "Valid email is required"
            binding.edemail.requestFocus()
            return false
        }

        if (password.isEmpty() || !isPasswordStrong(password)) {
            binding.edpass.error = "Password must be at least 8 characters long and include uppercase, lowercase, number, and special character"
            binding.edpass.requestFocus()
            return false
        }

        return true
    }

    private fun isPasswordStrong(password: String): Boolean {
        val passwordPattern = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,}$")
        return passwordPattern.matches(password)
    }

    private fun sendEmailVerification() {
        auth.currentUser?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Verification email sent.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleSignupError(exception: Exception?) {
        when (exception) {
            is FirebaseAuthUserCollisionException -> {
                Toast.makeText(this, "This email is already registered.", Toast.LENGTH_SHORT).show()
            }
            is FirebaseAuthInvalidCredentialsException -> {
                Toast.makeText(this, "Invalid email or password.", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Signup failed: ${exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUser(auth: FirebaseAuth, name: String, email: String, about: String) {
        val uid = auth.currentUser?.uid.toString()
        val user = User(uid, name, email, about)
        val myFireStore = FirebaseFirestore.getInstance()
        myFireStore.collection("Users").document(uid).set(user)
    }
}
