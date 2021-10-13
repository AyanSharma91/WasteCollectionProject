package com.ayansharma.wastecollectionproject.Util

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.Nullable
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import java.io.IOException
import java.lang.NullPointerException
import java.util.*


class LocationService : Service() {
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    lateinit var mRef : FirebaseDatabase


    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mRef= FirebaseDatabase.getInstance()
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
            startForeground(1, notification)
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
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                                   getAddress(this@LocationService,geoPoint.latitude,geoPoint.longitude)


                        }
                    }
                },
                Looper.myLooper()
            ) // Looper.myLooper tells this to repeat forever until thread is destroyed
        }


    fun getAddress( context : Context,  LATITUDE: Double,  LONGITUDE : Double){
        //Set Address
        try {
            var  geocoder =  Geocoder(context, Locale.getDefault())
            var addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);

            if (addresses != null && addresses.size > 0) {
                var address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                var city = addresses.get(0).getLocality();
                var state = addresses.get(0).getAdminArea();
                var  country = addresses.get(0).getCountryName();
                var postalCode = addresses.get(0).getPostalCode();
                var knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                Log.d(TAG, "getAddress:  address" + address)
                Log.d(TAG, "getAddress:  city" + city)
                Log.d(TAG, "getAddress:  state" + state)
                Log.d(TAG, "getAddress:  postalCode" + postalCode)
                Log.d(TAG, "getAddress:  knownName" + knownName)

                saveUserLocation(address,LATITUDE,LONGITUDE)
            }

        } catch ( e: IOException) {
            e.printStackTrace();
        }
        return
    }


    private fun saveUserLocation(address: String, latitude : Double, longitude : Double) {

        try {
            mRef.reference.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("address").setValue(address)
            mRef.reference.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("latitude").setValue(latitude)
            mRef.reference.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("longitude").setValue(longitude)
        }
        catch (e : NullPointerException)
        {
            stopSelf()
        }
    }

    companion object {
        private const val TAG = "LocationService"
        private const val UPDATE_INTERVAL = 4 * 1000 /* 4 secs */.toLong()
        private const val FASTEST_INTERVAL: Long = 2000 /* 2 sec */
    }
}