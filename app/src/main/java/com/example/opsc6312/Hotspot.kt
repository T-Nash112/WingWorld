package com.example.opsc6312

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class Hotspot : FragmentActivity(), OnMapReadyCallback {

    private val REQUEST_LOCATION_PERMISSION = 1
    private val ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION

    private lateinit var edtSearch: SearchView
    private lateinit var btnSearch: Button
    private lateinit var googleMap: GoogleMap

    private lateinit var navBar: BottomNavigationView

    var end_latitude = 0.0
    var end_longitude = 0.0
    var latitude = 0.0
    var longitude = 0.0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotspot)

        edtSearch = findViewById(R.id.srchView)
        btnSearch = findViewById(R.id.button)
        navBar = findViewById(R.id.navbar)

        //navigation bar
        navBar.setOnItemSelectedListener { menuIt ->
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

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        edtSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val location = edtSearch.query.toString()
                var addressList: List<Address>? = null

                if (location != null) {
                    val geocoder = Geocoder(this@Hotspot)
                    try {
                        addressList = geocoder.getFromLocationName(location, 1)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    if (addressList != null) {
                        if (addressList.isNotEmpty()) {
                            val address = addressList[0]
                            val latlng = LatLng(address.latitude, address.longitude)
                            googleMap.addMarker(MarkerOptions().position(latlng).title(location))
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 10f))
                        }
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        btnSearch.setOnClickListener {
            searchArea()
        }
    }

    private fun searchArea() {
        val location = edtSearch.query.toString()
        var addressList: List<Address>? = null

        if (location != "") {
            val geocoder = Geocoder(applicationContext)
            try {
                addressList = geocoder.getFromLocationName(location, 5)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (addressList != null) {
                for (i in addressList.indices) {
                    val myAddress = addressList[i]
                    val latlng = LatLng(myAddress.latitude, myAddress.longitude)
                    val markerOptions = MarkerOptions().position(latlng)
                    googleMap.addMarker(markerOptions)
                    val endLatitude = myAddress.latitude
                    val endLongitude = myAddress.longitude
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latlng))

                    val measure = intent.getStringExtra("measure").toString()
                    if(measure == "KM") {
                        val s =
                            calculateDistanceInKm(latitude, longitude, endLatitude, endLongitude)
                        val origin = MarkerOptions().position(LatLng(latitude, longitude))
                        origin.title(edtSearch.query.toString())
                        origin.snippet("Distance = $s $measure")
                        googleMap.addMarker(origin)

                        Toast.makeText(
                            this@Hotspot,
                            "Distance = $s $measure",
                            Toast.LENGTH_SHORT
                        ).show()

                        val url: String = getDirectionsUrl(LatLng(latitude, longitude), latlng)!!

                        val downloadTask: DownloadTask = DownloadTask()
                        downloadTask.execute(url)
                    } else if (measure == "Miles"){
                        val s = calculateDistanceInMiles(latitude, longitude, endLatitude, endLongitude)
                        val origin = MarkerOptions().position(LatLng(latitude, longitude))
                        origin.title(edtSearch.query.toString())
                        origin.snippet("Distance = $s $measure")
                        googleMap.addMarker(origin)

                        Toast.makeText(
                            this@Hotspot,
                            "Distance = $s $measure",
                            Toast.LENGTH_SHORT
                        ).show()

                        val url: String = getDirectionsUrl(LatLng(latitude, longitude), latlng)!!

                        val downloadTask: DownloadTask = DownloadTask()
                        downloadTask.execute(url)
                    }



                }
            }
        }
    }

    inner class DownloadTask : AsyncTask<String, Void, String>() {
        override fun onPostExecute(result: String) {
            super.onPostExecute(result)

            Log.d("Directions API Response", result)

            val parserTask = ParserTask()
            parserTask.execute(result)
        }

        override fun doInBackground(vararg url: String?): String {
            var data = ""
            try {
                data = downloadUrl(url[0].toString()).toString()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return data
        }
    }

    private fun downloadUrl(strUrl: String): String? {
        var data = ""
        var iStream: InputStream? = null
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL(strUrl)
            urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.connect()
            iStream = urlConnection.inputStream
            val br = BufferedReader(InputStreamReader(iStream))
            val sb = StringBuffer()
            var line: String? = null
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
            }
            data = sb.toString()
            br.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } finally {
            iStream?.close()
            urlConnection?.disconnect()
        }
        return data
    }

    inner class ParserTask : AsyncTask<String, Int, PolylineOptions?>() {
        override fun doInBackground(vararg jsonData: String?): PolylineOptions? {
            val jObject: JSONObject
            var polylineOptions: PolylineOptions? = null

            try {
                jObject = JSONObject(jsonData[0])
                val parser = DataParser()
                polylineOptions = parser.parseToPolyline(jObject)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return polylineOptions
        }

        override fun onPostExecute(result: PolylineOptions?) {
            if (result != null) {
                addPolylineToMap(result)
            }
        }
    }


    private fun calculateDistanceInKm(
        startLatitude: Double,
        startLongitude: Double,
        endLatitude: Double,
        endLongitude: Double
    ): String {
        val results = FloatArray(10)
        Location.distanceBetween(
            startLatitude, startLongitude, endLatitude, endLongitude, results
        )
        //val units = intent.getStringExtra("kilometers").toString()
        //return units
        return String.format("%.2f", results[0] / 1000) // Convert meters to kilometers
    }

    private fun calculateDistanceInMiles(
        startLatitude: Double,
        startLongitude: Double,
        endLatitude: Double,
        endLongitude: Double
    ): String {
        val results = FloatArray(10)
        Location.distanceBetween(
            startLatitude, startLongitude, endLatitude, endLongitude, results
        )
        //val units = intent.getStringExtra("kilometers").toString()
        //return units
        return String.format("%.2f", results[0] / 1609.34) // Convert meters to kilometers
    }

    private fun addPolylineToMap(polylineOptions: PolylineOptions) {
        googleMap.addPolyline(polylineOptions)
    }

    private fun getDirectionsUrl(origin: LatLng, dest: LatLng): String? {
        val str_origin = "origin=" + origin.latitude + "," + origin.longitude
        val str_dest = "destination=" + dest.latitude + "," + dest.longitude
        val mode = "mode=driving"
        val parameters = "$str_origin&$str_dest&$mode"
        val output = "json"
        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters&key=AIzaSyCwNu-scuV-63B37BWWv_4harHZtD6ypxg"
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        val width = 150 // Set your desired width in pixels
        val height = 150

        val walter = BitmapFactory.decodeResource(resources, R.drawable.waltersisulu)
        val walterMark = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(walter, width, height, false))

        googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(-26.086548, 27.844839))
                .title("Walter Sisulu National Botanical Garden")
                .icon(walterMark)
        )

        val rietvlei = BitmapFactory.decodeResource(resources, R.drawable.rietvlei)
        val rietvleiMark = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(rietvlei, width, height, false))

        googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(-25.8825, 28.2639))
                .title("Rietvlei Nature Reserve")
                .icon(rietvleiMark)
        )

        val roodeplat = BitmapFactory.decodeResource(resources, R.drawable.roodeplaat)
        val roodeplatMark = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(roodeplat, width, height, false))

        googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(-25.6308, 28.3167 ))
                .title("Roodeplaat dam nature reserve")
                .icon(roodeplatMark)
        )

        val austin = BitmapFactory.decodeResource(resources, R.drawable.austin)
        val austinMark = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(austin, width, height, false))

        googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(-25.770278, 28.227778 ))
                .title("Austin Roberts Bird Sanctuary")
                .icon(austinMark)
        )

        val abe = BitmapFactory.decodeResource(resources, R.drawable.abe)
        val abeMark = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(abe, width, height, false))

        googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(-26.304, 27.337 ))
                .title("Abe Bailey Nature Reserve")
                .icon(abeMark)
        )

        val dinokeng = BitmapFactory.decodeResource(resources, R.drawable.dinokeng)
        val dinokengMark = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(dinokeng, width, height, false))

        googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(-25.666667, 28.666667 ))
                .title("Dinokeng Game Reserve")
                .icon(dinokengMark)
        )

        val vaal = BitmapFactory.decodeResource(resources, R.drawable.vaal)
        val vaalMark = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(vaal, width, height, false))

        googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(-26.8760, 28.2263 ))
                .title("Vaal Marina")
                .icon(vaalMark)
        )

        val marie = BitmapFactory.decodeResource(resources, R.drawable.marievale)
        val marieMark = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(marie, width, height, false))

        googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(-26.359722, 28.508889 ))
                .title("Marievale")
                .icon(marieMark)
        )

        val suiker = BitmapFactory.decodeResource(resources, R.drawable.suiker)
        val suikerMark = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(suiker, width, height, false))

        googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(-26.4838, 28.2293 ))
                .title("Suikerbosrand Nature Reserve")
                .icon(suikerMark)
        )

        val ezem = BitmapFactory.decodeResource(resources, R.drawable.ezemvelo)
        val ezemMark = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(ezem, width, height, false))

        googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(-25.7086, 28.9299 ))
                .title("Ezemvelo Nature Reserve")
                .icon(ezemMark)
        )

        addCurrentLocationMarker(googleMap)
    }

    private fun addCurrentLocationMarker(googleMap: GoogleMap) {
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        } else {
            googleMap.isMyLocationEnabled = true

            googleMap.setOnMyLocationButtonClickListener {
                false
            }

            val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val userLocation = LatLng(location.latitude, location.longitude)

                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation))
                        latitude = location.latitude
                        longitude = location.longitude
                    }
                }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val mapFragment = supportFragmentManager
                    .findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this)
            }
        }
    }
}