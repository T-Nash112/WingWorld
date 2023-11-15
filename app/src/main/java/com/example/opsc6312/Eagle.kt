package com.example.opsc6312

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class Eagle : AppCompatActivity() {

    private lateinit var vdEagle: VideoView


    private lateinit var navbar: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eagle)

        vdEagle = findViewById(R.id.videoView)

        navbar = findViewById(R.id.navbarFacts)

        // Define your video URIs
        val eaglefacts = Uri.parse("android.resource://$packageName/${R.raw.baldeagle}")


        // Initialize and play the videos
        eagle(eaglefacts)



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

    private fun eagle(eaglefacts: Uri) {
        val mediaController = MediaController(this)
        vdEagle.setMediaController(mediaController)
        vdEagle.setVideoURI(eaglefacts)
        vdEagle.requestFocus()
        //vdEagle.start()
    }

}
