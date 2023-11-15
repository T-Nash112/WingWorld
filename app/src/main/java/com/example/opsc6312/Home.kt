package com.example.opsc6312

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.google.android.material.bottomnavigation.BottomNavigationView

class Home : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val birdsObservation = findViewById<ImageView>(R.id.img_observe)
        val explore = findViewById<ImageView>(R.id.img_explore)
        val btnPopular = findViewById<Button>(R.id.button4)
        val observation = findViewById<Button>(R.id.btnObservation)
        val btnexplore = findViewById<Button>(R.id.button2)
        val educate = findViewById<ImageView>(R.id.imageView)

        val navbar = findViewById<BottomNavigationView>(R.id.navbar)

        observation.setOnClickListener {
            val observe = Intent(this,Observations::class.java)
            startActivity(observe)
        }

        educate.setOnClickListener {
            val popular = Intent(this,PopularBirds::class.java)
            startActivity(popular)
        }

        btnexplore.setOnClickListener{
            val expBirds = Intent(this, Hotspot::class.java)
            startActivity(expBirds)
        }

        btnPopular.setOnClickListener {
            val popular = Intent(this,PopularBirds::class.java)
            startActivity(popular)
        }

        birdsObservation.setOnClickListener{
            val observe = Intent(this,Observations::class.java)
            startActivity(observe)
        }

        explore.setOnClickListener{
            val expBirds = Intent(this, Hotspot::class.java)
            startActivity(expBirds)
        }

        navbar.setOnItemSelectedListener {menuItem->
            //menuItem is like a variable
            when(menuItem.itemId){
                R.id.menu_Home -> {
                    val toHome = Intent(this,Home::class.java)
                    startActivity(toHome)

                    true
                }
                R.id.menu_Menu->{
                    val toObserve = Intent(this,Observations::class.java)
                    startActivity(toObserve)
                    true
                }
                R.id.menu_Explore -> {
                    val toExplore = Intent(this, Hotspot::class.java)
                    startActivity(toExplore)
                    true
                }
                R.id.menu_Location->{
                    val toLocation = Intent(this, ExploreBirds::class.java)
                    startActivity(toLocation)
                    true
                }
                R.id.menu_Settings->{
                    val sendAccount = intent.getStringExtra("account")
                    val toSetting = Intent(this, Settings::class.java)
                    toSetting.putExtra("account", sendAccount)
                    startActivity(toSetting)
                    true

                }
                else -> false

            }

        }
    }
}