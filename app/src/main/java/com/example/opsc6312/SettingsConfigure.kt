package com.example.opsc6312

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.ToggleButton
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.FirebaseDatabase

class SettingsConfigure : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_configure)

        val toggle = findViewById<ToggleButton>(R.id.tbUnits)
        val setMax = findViewById<Button>(R.id.btnSet)
        val maxDist = findViewById<EditText>(R.id.edtMaxDistance)
        val navbar = findViewById<BottomNavigationView>(R.id.configNavbar)

        navbar.setOnItemSelectedListener {configItem->
            //menuItem is like a variable
            when(configItem.itemId){
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
                    val toSetting = Intent(this, Settings::class.java)
                    startActivity(toSetting)
                    true

                }
                else -> false

            }

        }

        setMax.setOnClickListener {
            val maximumDistance = maxDist.text.toString()

            val databaseRef = FirebaseDatabase.getInstance().getReference("MaximumDistance")
            databaseRef.setValue(maximumDistance)

            Toast.makeText(this, "You set your max distance to $maximumDistance", Toast.LENGTH_SHORT).show()
        }

        toggle.setOnCheckedChangeListener{_,isChecked->
            if(isChecked){
                val kilometers = 1000.0 // Replace with your desired value
                val formattedResult = convertKilometersToFormattedString(kilometers)
                val measureUnit = "KM"

                val intent = Intent(this, Hotspot::class.java)
                intent.putExtra("kilometers", formattedResult)
                intent.putExtra("measure",measureUnit)
                startActivity(intent)
            }else{
                val kilometers = 1000.0 // Replace with your desired value
                val formattedResult = convertKilometersToMilesAndFormat(kilometers)
                val measureUnit = "Miles"

                val intent = Intent(this, Hotspot::class.java)
                intent.putExtra("kilometers", formattedResult)
                intent.putExtra("measure",measureUnit)
                startActivity(intent)
            }
        }
    }
    fun convertKilometersToFormattedString(miles: Double): String {
        val kilometers =  (miles * 1.60934)
        return String.format("%.2f", kilometers)
    }

    fun convertKilometersToMilesAndFormat(kilometers: Double): String {
        val miles = (kilometers * 0.62137119)
        return String.format("%.2f", miles)
    }
}