package com.example.opsc6312

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class Profile : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 1

    private var imgUri: Uri? = null

    private var mStorageRef: StorageReference? = null
    private var databaseRef: DatabaseReference? = null

    private lateinit var profile: ImageView
    private lateinit var proEmail: TextView
    private lateinit var setProfile: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val account = intent.getStringExtra("accountFinal")

        proEmail = findViewById(R.id.proEmail)
        profile = findViewById(R.id.imgProfile)
        setProfile = findViewById(R.id.setProfile)

        proEmail.text = account

        mStorageRef = FirebaseStorage.getInstance().getReference("profiles")
        databaseRef = FirebaseDatabase.getInstance().getReference("profiles")

        profile.setOnClickListener{
            openFileChooser()

        }

        setProfile.setOnClickListener {
            uploadFile()
        }

        val navbar = findViewById<BottomNavigationView>(R.id.navbarPro)

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
    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null
            && data.data != null
        ) {
            imgUri = data.data
            profile.setImageURI(imgUri)
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        val cR: ContentResolver = contentResolver
        val mime: MimeTypeMap = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri))
    }

    private fun uploadFile() {
        if (imgUri != null) {
            val fileReference: StorageReference? =
                mStorageRef?.child("${System.currentTimeMillis()}.${getFileExtension(imgUri!!)}")

            fileReference?.putFile(imgUri!!)
                ?.addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                        Toast.makeText(this, "Successfully Uploaded", Toast.LENGTH_LONG).show()
                        val upload = Upload(proEmail.text.toString().trim(), uri.toString())
                        val uploadId: String? = databaseRef?.push()?.key
                        uploadId?.let { databaseRef?.setValue(upload) }
                    }
                }
                ?.addOnFailureListener { e ->
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Please select a File", Toast.LENGTH_SHORT).show()
        }
    }
}