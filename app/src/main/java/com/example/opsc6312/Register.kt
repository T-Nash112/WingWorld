package com.example.opsc6312

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Register : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btnReg = findViewById<Button>(R.id.regs1btn)
        val fname = findViewById<EditText>(R.id.edtF_name)
        val dOb = findViewById<EditText>(R.id.edt_dateOfBirth)
        val email = findViewById<EditText>(R.id.edt_email)
        val userName = findViewById<EditText>(R.id.edt_username_reg)
        val password = findViewById<EditText>(R.id.edt_password_reg)
        val conPassword = findViewById<EditText>(R.id.edt_conPassword)

        val signIn = findViewById<TextView>(R.id.signInLink)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("users")

        btnReg.setOnClickListener {

            val firstName = fname.text.toString()
            val dateOfBirth = dOb.text.toString()
            val email = email.text.toString()
            val username = userName.text.toString()
            val password = password.text.toString()
            val confirmPass = conPassword.text.toString()

            if (firstName.isEmpty() || dateOfBirth.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPass.isEmpty())
            {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPass) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else{
                // Create user in Firebase Authentication
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val currentUser: FirebaseUser? = auth.currentUser
                            if (currentUser != null) {
                                val userId = currentUser.uid
                                val user = User(firstName, username, email, dateOfBirth, password)
                                // Save user data in Firebase Realtime Database
                                database.child(userId).setValue(user)
                                Toast.makeText(
                                    this,
                                    "User created successfully",
                                    Toast.LENGTH_SHORT
                                ).show()

                                // Redirect to login page
                                val login = Intent(this, Login::class.java)
                                startActivity(login)
                            }
                        } else {
                            Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                        }
                    }


            }

        }

        signIn.setOnClickListener {
            val toLogin = Intent(this, Login::class.java)
            startActivity(toLogin)
        }
    }
}