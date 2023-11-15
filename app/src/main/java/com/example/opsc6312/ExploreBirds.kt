package com.example.opsc6312

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ExploreBirds : AppCompatActivity() {

    private lateinit var btnAdd: Button
    private lateinit var edtJournal: EditText

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: JournalAdapter

    private lateinit var navbar: BottomNavigationView

    private val databaseReference = FirebaseDatabase.getInstance().getReference("Journal")

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore_birds)

        btnAdd = findViewById(R.id.button3)
        edtJournal = findViewById(R.id.edtJournal)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        navbar = findViewById(R.id.navbar)

        val textItems = mutableListOf<Journal>()

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                textItems.clear()
                for (snapshot in dataSnapshot.children) {
                    val text = snapshot.child("text").getValue(String::class.java) ?: ""
                    textItems.add(Journal(text))
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
                Toast.makeText(
                    this@ExploreBirds,
                    "Failed to add patient history",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        databaseReference.addValueEventListener(valueEventListener)

        adapter = JournalAdapter(textItems)
        recyclerView.adapter = adapter

        btnAdd.setOnClickListener {
            saveJournal()
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

    private fun saveJournal() {
        val journal = edtJournal.text.toString().trim()

        if (journal.isNotEmpty()) {
            try {
                val databaseRef = FirebaseDatabase.getInstance().getReference("Journal")
                val upload = Journal(journal)
                val uploadId: String? = databaseRef.push().key // Use push() to generate a unique key

                uploadId?.let {
                    databaseRef.child(it).setValue(upload)
                }

                // Display a success message
                Toast.makeText(this, "Updated Journal saved: $journal", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                // Handle the exception (e.g., log the error and show an error message)
                Log.e("FirebaseError", "Error saving journal: ${e.message}", e)
                Toast.makeText(this, "Failed to save journal", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Display an error message if the treatment name is empty
            Toast.makeText(this, "Please enter a valid input", Toast.LENGTH_SHORT).show()
        }
    }
}