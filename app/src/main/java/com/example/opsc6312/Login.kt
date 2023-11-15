package com.example.opsc6312

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailLogin = findViewById<EditText>(R.id.edt_email_log)
        val passwordLogin = findViewById<EditText>(R.id.edt_password_log)

        val signUp = findViewById<TextView>(R.id.signUpLink)

        val btnLog = findViewById<Button>(R.id.loginbtn)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("users")

        btnLog.setOnClickListener {

            val email = emailLogin.text.toString()
            val password = passwordLogin.text.toString()

            //db
            if(email.isNotBlank() && password.isNotBlank()) {

                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {

                            Toast.makeText(this,"Successfully Logged In", Toast.LENGTH_SHORT).show()
                            val toHome = Intent(this,Home::class.java)
                            toHome.putExtra("account", email)
                            startActivity(toHome)
                            finish()
                        } else {

                            Toast.makeText(this,"Please enter correct login details", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
            else{
                Toast.makeText(this,"Please fill in empty fields",Toast.LENGTH_SHORT).show()
            }
                }

        signUp.setOnClickListener {
            val toRegister = Intent(this,Register::class.java)
            startActivity(toRegister)
        }

            }
        }
