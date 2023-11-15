package com.example.opsc6312

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.google.android.material.bottomnavigation.BottomNavigationView

class PopularBirds : AppCompatActivity() {

    private lateinit var imgBird2: ImageView
    private lateinit var imgBird4: ImageView
    private lateinit var imgBird5: ImageView
    private lateinit var imgBird6: ImageView

    private lateinit var navbar:BottomNavigationView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_popular_birds)

        imgBird2 = findViewById(R.id.imageButton2)
        imgBird4 = findViewById(R.id.imageButton4)
        imgBird5 = findViewById(R.id.imageButton5)
        imgBird6 = findViewById(R.id.imageButton6)

        navbar = findViewById(R.id.navbar)

        imgBird2.setOnClickListener{
            val intent = Intent(this, Eagle::class.java)
            startActivity(intent)
        }

        imgBird4.setOnClickListener{
            val intent = Intent(this, HawkVsEagle::class.java)
            startActivity(intent)
        }

        imgBird5.setOnClickListener{
            val intent = Intent(this, Humming::class.java)
            startActivity(intent)
        }

        imgBird6.setOnClickListener{
            val intent = Intent(this, Top5::class.java)
            startActivity(intent)
        }

        navbar.setOnItemSelectedListener { menuIt ->
            //menuItem is like a variable
            when (menuIt.itemId) {
                R.id.menu_Home -> {
                    val toHome = Intent(this, Home::class.java)
                    startActivity(toHome)

                    true
                }

                R.id.menu_Menu -> {
                    val toObserve = Intent(this, Observations::class.java)
                    startActivity(toObserve)
                    true
                }

                R.id.menu_Explore -> {
                    val toExplore = Intent(this, Hotspot::class.java)
                    startActivity(toExplore)
                    true
                }

                R.id.menu_Location -> {
                    val toLocation = Intent(this, ExploreBirds::class.java)
                    startActivity(toLocation)
                    true
                }

                R.id.menu_Settings -> {
                    val toSetting = Intent(this, Settings::class.java)
                    startActivity(toSetting)
                    true

                }

                else -> false

            }

        }
    }
}