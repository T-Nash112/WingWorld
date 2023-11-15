package com.example.opsc6312

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class Observations : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 1

    private lateinit var btnUpload: Button
    private lateinit var btnView: Button
    private lateinit var btnAdd: Button
    private lateinit var edtObserve: EditText
    private lateinit var imgView: ImageView

    private var imgUri: Uri? = null

    private var mStorageRef: StorageReference? = null
    private var databaseRef: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_observations)

        btnUpload = findViewById(R.id.btnUpload)
        btnView = findViewById(R.id.btnView)
        btnAdd = findViewById(R.id.btnAdd)
        edtObserve = findViewById(R.id.edtObser)
        imgView = findViewById(R.id.imgView)
        val navbar = findViewById<BottomNavigationView>(R.id.navbar)

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



        mStorageRef = FirebaseStorage.getInstance().getReference("uploads")
        databaseRef = FirebaseDatabase.getInstance().getReference("uploads")

        btnUpload.setOnClickListener {

            uploadFile()
        }

        btnView.setOnClickListener {

            openImageActivity()
        }

        btnAdd.setOnClickListener {
            openFileChooser()
        }
    }
    private fun openImageActivity() {
        val intent = Intent(this, ViewObservations::class.java)
        startActivity(intent)
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


            imgView.setImageURI(imgUri)
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
                        val upload = Upload(edtObserve.text.toString().trim(), uri.toString())
                        val uploadId: String? = databaseRef?.push()?.key
                        uploadId?.let { databaseRef?.child(it)?.setValue(upload) }
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