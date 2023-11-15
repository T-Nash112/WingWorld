package com.example.opsc6312

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.material.bottomnavigation.BottomNavigationView

class Settings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val navbar = findViewById<BottomNavigationView>(R.id.navbar)

        val btnView = findViewById<Button>(R.id.btnViewProfile)
        val configureSets = findViewById<Button>(R.id.btnConfigure)
        val logout = findViewById<Button>(R.id.btnLogout)

        configureSets.setOnClickListener {
            val intent = Intent(this, SettingsConfigure::class.java)
            startActivity(intent)
        }

        btnView.setOnClickListener {
            val account = intent.getStringExtra("account")
            val intent = Intent(this, Profile::class.java)
            intent.putExtra("accountFinal", account)
            startActivity(intent)
        }

        logout.setOnClickListener {
            val intent = Intent(this, Login::class.java)
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
