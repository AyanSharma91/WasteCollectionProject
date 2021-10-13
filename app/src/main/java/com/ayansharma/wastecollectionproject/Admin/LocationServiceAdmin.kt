package com.ayansharma.wastecollectionproject.Admin

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.core.app.ActivityCompat
import com.ayansharma.wastecollectionproject.Activities.LoginActivity
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.GeoPoint


class LocationServiceAdmin : Service() {
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    lateinit var mRef: FirebaseDatabase


    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mRef = FirebaseDatabase.getInstance()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (Build.VERSION.SDK_INT >= 26) {
            val CHANNEL_ID = "my_channel_01"
            val channel = NotificationChannel(
                CHANNEL_ID,
                "My Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                channel
            )
            val notification: Notification = Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("")
                .setContentText("").build()
            startForeground(2, notification)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: called.")
        location

        //START_NOT_STICKY represents commands to be running till when command is send to them
        return START_NOT_STICKY
    }

    // ---------------------------------- LocationRequest ------------------------------------
    // Create the location request to start receiving updates
    private val location:


    // new Google API SDK v11 uses getFusedLocationProviderClient(this)
    // Looper.myLooper tells this to repeat forever until thread is destroyed
            Unit
        private get() {

            // ---------------------------------- LocationRequest ------------------------------------
            // Create the location request to start receiving updates
            val mLocationRequestHighAccuracy = LocationRequest()
            mLocationRequestHighAccuracy.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            mLocationRequestHighAccuracy.interval = UPDATE_INTERVAL
            mLocationRequestHighAccuracy.fastestInterval = FASTEST_INTERVAL


            // new Google API SDK v11 uses getFusedLocationProviderClient(this)
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d(
                    TAG,
                    "getLocation: stopping the location service."
                )
                stopSelf()
                return
            }
            Log.d(
                TAG,
                "getLocation: getting location information."
            )
            mFusedLocationClient!!.requestLocationUpdates(
                mLocationRequestHighAccuracy, object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        Log.d(
                            TAG,
                            "onLocationResult: got location result."
                        )
                        val location = locationResult.lastLocation
                        if (location != null) {

                            var geoPoint = GeoPoint(location.latitude, location.longitude)
                            //getAddress(this@LocationServiceAdmin,geoPoint.latitude,geoPoint.longitude)
                            saveUserLocation(geoPoint.latitude, geoPoint.longitude)


                        }
                        else {
                            Toast.makeText(
                                this@LocationServiceAdmin,
                                "Location not detected  TRY AGAIN........",
                                Toast.LENGTH_LONG
                            ).show()
                            val intent = Intent(this@LocationServiceAdmin, LoginActivity::class.java)
                            startActivity(intent)
                        }
                    }
                },
                Looper.myLooper()
            ) // Looper.myLooper tells this to repeat forever until thread is destroyed
        }


    private fun saveUserLocation(latitude: Double, longitude: Double) {


        try {

            var sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
            var userName: String? = sharedPref.getString("userName", "Not Available")

            mRef.reference.child("Admin Users").child(userName!!).child("Location")
                .child("admin_geo_Point")
                .child("latitude").setValue(latitude)
            mRef.reference.child("Admin Users").child(userName).child("Location")
                .child("admin_geo_Point")
                .child("longitude").setValue(longitude)

        } catch (e: NullPointerException) {

            stopSelf()
        }
    }

    companion object {
        private const val TAG = "LocationService"
        private const val UPDATE_INTERVAL = 4 * 1000 /* 4 secs */.toLong()
        private const val FASTEST_INTERVAL: Long = 2000 /* 2 sec */
    }

}

