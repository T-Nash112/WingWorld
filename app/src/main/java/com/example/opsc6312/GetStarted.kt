package com.example.opsc6312

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class GetStarted : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_started)

        val btnGet = findViewById<Button>(R.id.getstartedbtn)

        btnGet.setOnClickListener {
            val intent = Intent(this, TsAndCs::class.java)
            startActivity(intent)
        }
    }
}