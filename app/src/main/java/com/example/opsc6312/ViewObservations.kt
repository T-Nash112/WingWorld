package com.example.opsc6312

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ViewObservations : AppCompatActivity() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: ImageAdapter

    private lateinit var mDatabaseRef: DatabaseReference
    private var mUploads: MutableList<Upload> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_observations)

        mRecyclerView = findViewById(R.id.recycler_view)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads")

        mDatabaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children) {
                    val upload = postSnapshot.getValue(Upload::class.java)
                    mUploads.add(upload!!)
                }

                mAdapter = ImageAdapter(this@ViewObservations, mUploads)
                mRecyclerView.adapter = mAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@ViewObservations, databaseError.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

}