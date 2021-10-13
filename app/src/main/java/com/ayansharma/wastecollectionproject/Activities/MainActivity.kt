package com.ayansharma.wastecollectionproject.Activities

import android.Manifest
import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.ayansharma.wastecollectionproject.Admin.User_Location_Admin
import com.ayansharma.wastecollectionproject.R
import com.ayansharma.wastecollectionproject.Util.AlertReceiver
import com.ayansharma.wastecollectionproject.Util.AlertReceiver2
import com.ayansharma.wastecollectionproject.Util.Constants
import com.ayansharma.wastecollectionproject.Util.LocationService
import com.ayansharma.wastecollectionproject.fragments.Dashboard
import com.ayansharma.wastecollectionproject.fragments.SignOutFragment
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.GeoPoint
import java.io.IOException
import java.util.*


/*
----------------------------WE NEED TO DO THE ALL THE PERMISSIONS AND CHECKS AGAIN HERE AS WE HAVE ALREADY DONE IN ADMIN BECAUSE THAT WILL
                           BE ONLY RESPONSIBLE FOR THE ADMIN LOCATION AND STORING HIS OR HER COORDINTAES IN THE DATABASE BUT ALSO WE HAVE TO
                           STORE THE COORDINATES  OF THE USER ONCE HE HAS LOGGED IN SO WE WILL HAVE TO CHECK FOR ALL THE PROCESS HERE ALSO
 */



class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var frame: FrameLayout
    lateinit var navigationView: NavigationView
    lateinit var myToolbar: Toolbar
    var previousMenuItem : MenuItem?=null

    lateinit var  mFusedLocationClient : FusedLocationProviderClient
    var constants= Constants()
    var mLocationPermissionGranted=false
    var TAG= "This is what you wanted"
     var mUserLocation : User_Location_Admin?= null
    lateinit var manager: NotificationManager

    private val name: CharSequence? = "ayan"
    private val CHANNEL_ID: String? = "123"
    private val CHANNEL_ID2 : String?="1234"



    lateinit var calender : Calendar
    lateinit var calender2 : Calendar


    lateinit var mRef : FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        manager = this@MainActivity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        mFusedLocationClient= LocationServices.getFusedLocationProviderClient(this@MainActivity)

        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        frame = findViewById(R.id.frame)
        navigationView = findViewById(R.id.navigationView)
        myToolbar = findViewById(R.id.myToolbar)

        mRef= FirebaseDatabase.getInstance()




        setUpToolbar()

                  logOutFunctality()
        supportFragmentManager.beginTransaction().replace(
            R.id.frame,
            Dashboard()
        ).commit()
        supportActionBar?.title = "उपयोगकर्ता जानकारी"
        navigationView.setCheckedItem(R.id.dashboard)
        drawerLayout.closeDrawers()

        //It syncs the action Bar to  the navigation drawer
        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity, drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()



        navigationView.setNavigationItemSelectedListener {



            if(previousMenuItem!=null) {
                previousMenuItem?.isChecked = false

                it.isCheckable=true
                it.isChecked=true
                previousMenuItem=it
            }
            when (it.itemId) {
                R.id.dashboard -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frame,
                        Dashboard()
                    ).commit()
                    navigationView.setCheckedItem(R.id.dashboard)
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "उपयोगकर्ता जानकारी"
                }

                R.id.sign_Out -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frame,
                        SignOutFragment()
                    ).commit()
                    navigationView.setCheckedItem(R.id.sign_Out)
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "प्रस्थान करें"
                }
            }
            return@setNavigationItemSelectedListener true
        }


        if(checkMapServices())
            if(mLocationPermissionGranted)
            {
                // getChatrooms()
                getLastKnownLocation()

            }
            else
            {   getLocationPermission()
                if(checkMapServices())
                {
                    if(mLocationPermissionGranted)
                        getLastKnownLocation()
                }
            }




    }

    fun startAlarm(c: Calendar)
    {
        var alarmManagerr : AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var intent = Intent(this, AlertReceiver::class.java)
        var  pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0)
        alarmManagerr.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent)



    }

    fun startAlarm2(c: Calendar)
    {
        var alarmManagerr2 : AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var intent2 = Intent(this, AlertReceiver2::class.java)
        var  pendingIntent2 = PendingIntent.getBroadcast(this, 90, intent2, 0)
        alarmManagerr2.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis, pendingIntent2)



    }








    /*
--------------------The code detects Notifications as a Service -------------------------------------------------------------
 */
    private fun startLocationService() {
        if (!isLocationServiceRunning()) {
            val serviceIntent = Intent(this, LocationService::class.java)
            //        this.startService(serviceIntent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }
        }
    }

    private fun isLocationServiceRunning(): Boolean {
        val manager: ActivityManager =
            getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if ("com.ayansharma.wastecollectionproject.Util.LocationService" == service.service.getClassName()) {
                Log.d(
                    TAG,
                    "isLocationServiceRunning: location service is already running."
                )
                return true
            }
        }
        Log.d(TAG, "isLocationServiceRunning: location service is not running.")
        return false
    }




    fun saveMyLocation(address: String, latitude: Double, longitude: Double)
    {

         mRef.reference.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid).child("address").setValue(
             address
         )
         mRef.reference.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid).child("latitude").setValue(
             latitude
         )
         mRef.reference.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid).child("longitude").setValue(
             longitude
         )
        Toast.makeText(
            this@MainActivity,
            "आपका स्थान संग्रहीत किया गया है .....",
            Toast.LENGTH_LONG
        ).show()
    }




    /*
----------------------Getting the last known location of the current device------------------------------------------
*/


   fun getLastKnownLocation(){
       if (ActivityCompat.checkSelfPermission(
               this,
               Manifest.permission.ACCESS_FINE_LOCATION
           ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
               this,
               Manifest.permission.ACCESS_COARSE_LOCATION
           ) != PackageManager.PERMISSION_GRANTED
       ) {
           // TODO: Consider calling
           //    ActivityCompat#requestPermissions
           // here to request the missing permissions, and then overriding
           //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
           //                                          int[] grantResults)
           // to handle the case where the user grants the permission. See the documentation
           // for ActivityCompat#requestPermissions for more details.
           return
       }
       mFusedLocationClient.lastLocation.addOnCompleteListener { task ->
            if(task.isSuccessful) {

                var location= task.result
               if(location!=null) {
                    var geoPoint = GeoPoint(location?.latitude!!, location.longitude)

                    if (geoPoint != null)
                        mUserLocation?.geo_Point = geoPoint
                    else
                        mUserLocation?.geo_Point = null
                    getAddress(this@MainActivity, geoPoint.latitude, geoPoint.longitude)
                }
                else
               {

                   Toast.makeText(
                       this@MainActivity,
                       "\n" +
                               "आपका स्थान नहीं मिला है कृपया एप्लिकेशन पुनः आरंभ करें ",
                       Toast.LENGTH_LONG
                   ).show()

                   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                       manager.deleteNotificationChannel(CHANNEL_ID)
                       manager.deleteNotificationChannel(CHANNEL_ID2)
                   }

                   manager.cancel(1)
                   manager.cancel(5)
                   FirebaseAuth.getInstance().signOut()
                   val intent = Intent(this@MainActivity, LoginActivity::class.java)
                   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                   startActivity(intent)

                }


            }
        }
   }

     fun getAddress(context: Context, LATITUDE: Double, LONGITUDE: Double){
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

                saveMyLocation(address, LATITUDE, LONGITUDE)

                calender= Calendar.getInstance()
                calender.set(Calendar.HOUR_OF_DAY, 5)
                calender.set(Calendar.MINUTE, 0)
                calender.set(Calendar.SECOND, 0)

                calender2= Calendar.getInstance()
                calender2.set(Calendar.HOUR_OF_DAY, 5)
                calender2.set(Calendar.MINUTE, 0)
                calender2.set(Calendar.SECOND, 0)


                if((FirebaseAuth.getInstance().currentUser)  !=null) {
                    if (intent.getStringExtra("FLAG-1") == "otp") {
                        startAlarm(calender)
                        startAlarm2(calender2)
                    }
                    else {
                        var alarmManager: AlarmManager =
                            getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        var intent = Intent(this, AlertReceiver::class.java)
                        var pendingIntent = PendingIntent.getBroadcast(this, 2, intent, 0)

                        var intent2 = Intent(this, AlertReceiver2::class.java)
                        var pendingIntent2 = PendingIntent.getBroadcast(this, 15, intent2, 0)
                        val firingCal2= Calendar.getInstance()
                        val currentCal2= Calendar.getInstance()
                        val firingCal = Calendar.getInstance()
                        val currentCal = Calendar.getInstance()

                        firingCal[Calendar.HOUR_OF_DAY] = 12// At the hour you wanna fire

                        firingCal[Calendar.MINUTE] = 52// Particular minute

                        firingCal[Calendar.SECOND] = 0 // particular second

                        firingCal2[Calendar.HOUR_OF_DAY] = 13// At the hour you wanna fire

                        firingCal2[Calendar.MINUTE] = 56 // Particular minute

                        firingCal2[Calendar.SECOND] = 0 // particular second


                        if (firingCal2.compareTo(currentCal2) < 0) {
                            firingCal2.add(Calendar.DAY_OF_MONTH, 1)
                        }
                        val intendedTime2 = firingCal2.timeInMillis
                        alarmManager.setRepeating(
                            AlarmManager.RTC,
                            intendedTime2,
                            AlarmManager.INTERVAL_DAY,
                            pendingIntent2
                        )




                        if (firingCal.compareTo(currentCal) < 0) {
                            firingCal.add(Calendar.DAY_OF_MONTH, 1)
                        }
                        val intendedTime = firingCal.timeInMillis
                        alarmManager.setRepeating(
                            AlarmManager.RTC,
                            intendedTime,
                            AlarmManager.INTERVAL_DAY,
                            pendingIntent
                        )
                    }
                }





                startLocationService()

            }

        } catch (e: IOException) {
            e.printStackTrace();
        }
        return
    }






    /*
    -----------------------------setting up the toolbar---------------------------------------------------------------------------
     */

    fun setUpToolbar() {
        setSupportActionBar(myToolbar)
        supportActionBar?.title = "उपयोगकर्ता जानकारी"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)

        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.frame)
        when (fragment) {
            !is Dashboard -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.frame,
                    Dashboard()
                ).commit()
                supportActionBar?.title = "उपयोगकर्ता जानकारी"
                navigationView.setCheckedItem(R.id.dashboard)
                drawerLayout.closeDrawers()
            }
            else -> super.onBackPressed()
        }
    }



    /*
  -------------------Asking for permissions for accessing the network state ,location---------------------------------------
   */

    private fun checkMapServices(): Boolean {
        if (isServicesOK()) {
            if (isMapsEnabled()) {
                return true
            }
        }
        return false
    }

    private fun buildAlertMessageNoGps() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("इस एप्लिकेशन को ठीक से काम करने के लिए GPS की आवश्यकता है, क्या आप इसे सक्षम करना चाहते हैं?")
            .setCancelable(false)
            .setPositiveButton("हाँ", DialogInterface.OnClickListener { dialog, id ->
                val enableGpsIntent =
                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivityForResult(enableGpsIntent, constants.PERMISSIONS_REQUEST_ENABLE_GPS)
            })
        val alert: AlertDialog = builder.create()
        alert.show()
    }

    fun isMapsEnabled(): Boolean {
        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
            return false
        }
        return true
    }

    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            mLocationPermissionGranted = true

            getLastKnownLocation()

        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    fun isServicesOK(): Boolean {
        Log.d(TAG, "isServicesOK: checking google services version")
        val available =
            GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this@MainActivity)
        if (available == ConnectionResult.SUCCESS) {

            Log.d(TAG, "isServicesOK: Google Play Services is working")
            return true
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {

            Log.d(TAG, "isServicesOK: an error occured but we can fix it")
            val dialog: Dialog = GoogleApiAvailability.getInstance()
                .getErrorDialog(this@MainActivity, available, constants.ERROR_DIALOG_REQUEST)
            dialog.show()
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show()
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        mLocationPermissionGranted = false
        when (requestCode) {
            constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    mLocationPermissionGranted = true
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: called.")
        when (requestCode) {
            constants.PERMISSIONS_REQUEST_ENABLE_GPS -> {
                if (mLocationPermissionGranted) {

                    getLastKnownLocation()
                } else {
                    getLocationPermission()
                }
            }
        }
    }




    override fun onStart() {
        super.onStart()
        getLastKnownLocation()
        if(checkMapServices())
            if(mLocationPermissionGranted)
            {

                getLastKnownLocation()

            }
            else
            {   getLocationPermission()
                if(checkMapServices())
                {
                    if(mLocationPermissionGranted)
                        getLastKnownLocation()
                }
            }

    }


     fun logOutFunctality() {
         val timer: CountDownTimer = object : CountDownTimer(1000*60*60*8, 1000) {
             override fun onTick(millisUntilFinished: Long) {
                 //Some code
             }

             override fun onFinish() {
                 manager =
                     (this@MainActivity).getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                 var alarmManager: AlarmManager =
                     (this@MainActivity).getSystemService(Context.ALARM_SERVICE) as AlarmManager



                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                     manager.deleteNotificationChannel(CHANNEL_ID)
                     manager.deleteNotificationChannel(CHANNEL_ID2)
                 }

                 manager.cancel(1)
                 manager.cancel(5)
                 var intent2 = Intent(this@MainActivity, AlertReceiver::class.java)
                 var pendingIntent = PendingIntent.getBroadcast(
                     this@MainActivity,
                     2,
                     intent2,
                     PendingIntent.FLAG_UPDATE_CURRENT
                 )
                 alarmManager.cancel(pendingIntent)


                 var alarmManagerr: AlarmManager =
                     (this@MainActivity).getSystemService(Context.ALARM_SERVICE) as AlarmManager
                 var intent3 = Intent(this@MainActivity, AlertReceiver::class.java)
                 var pendingIntent2 = PendingIntent.getBroadcast(
                     this@MainActivity,
                     1,
                     intent3,
                     PendingIntent.FLAG_UPDATE_CURRENT
                 )
                 alarmManagerr.cancel(pendingIntent2)


                 var alarmManagerr8: AlarmManager =
                     (this@MainActivity).getSystemService(Context.ALARM_SERVICE) as AlarmManager
                 var intent4 = Intent(this@MainActivity, AlertReceiver2::class.java)
                 var pendingIntent6 = PendingIntent.getBroadcast(
                     this@MainActivity,
                     15,
                     intent4,
                     PendingIntent.FLAG_UPDATE_CURRENT
                 )
                 alarmManagerr8.cancel(pendingIntent6)


                 var alarmManagerr9: AlarmManager =
                     (this@MainActivity).getSystemService(Context.ALARM_SERVICE) as AlarmManager
                 var intent5 = Intent(this@MainActivity, AlertReceiver2::class.java)
                 var pendingIntent3 = PendingIntent.getBroadcast(
                     this@MainActivity,
                     90,
                     intent5,
                     PendingIntent.FLAG_UPDATE_CURRENT
                 )
                 alarmManagerr9.cancel(pendingIntent3)


                 var mAuth = FirebaseAuth.getInstance()
                 mAuth.signOut()
                 val intent = Intent(this@MainActivity, LoginActivity::class.java)
                 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                 startActivity(intent)

             }}

         timer.start()

     }




}