package com.ayansharma.wastecollectionproject.Admin

import android.Manifest
import android.app.ActivityManager
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ayansharma.wastecollectionproject.Activities.LoginActivity
import com.ayansharma.wastecollectionproject.R
import com.ayansharma.wastecollectionproject.Util.Constants
import com.ayansharma.wastecollectionproject.Util.LocationService
import com.ayansharma.wastecollectionproject.model.Admin_User_Location
import com.ayansharma.wastecollectionproject.model.User
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.synthetic.main.admin_authentication.*



class Admin_Authentication :AppCompatActivity() {


    private val ps1 ="WasteCollection@123"
    private val ps2= "admin@123"
    var TAG= "This is what you wanted"

    var mLocationPermissionGranted=false
    var constants= Constants()
    lateinit var progressBar :ProgressBar
    lateinit var  mFusedLocationClient : FusedLocationProviderClient
    var madminUserLocation : Admin_User_Location?=null
    lateinit var mRef : FirebaseDatabase
    lateinit var adminName : String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_authentication)
            progressBar = findViewById(R.id.progressBar2)
        progressBar.visibility = View.GONE
        mFusedLocationClient= LocationServices.getFusedLocationProviderClient(this@Admin_Authentication)
        mRef= FirebaseDatabase.getInstance()




        btn_login.setOnClickListener {

           adminName = admin_name.text.toString()
            var password = name.text.toString()
            if(password==ps1 || password==ps2)
            {
                progressBar.visibility= View.VISIBLE



                if(checkMapServices())
                    if(mLocationPermissionGranted)
                    {
                        getUserdetails()

                    }
                    else
                    {
                        getLocationPermission()
                    }

                val intent = Intent(this@Admin_Authentication , Admin_usersActivity::class.java)
                intent.putExtra("NameOfTheAdmin",adminName)
                startActivity(intent)
                progressBar.visibility = View.GONE
            }
            else
            {
                Toast.makeText(this@Admin_Authentication, "Authentication failed...",Toast.LENGTH_LONG).show()
            }



        }



    }








    /*
   ----------------------------here we will take the user object data into our user-Location object---------------------------------------
    */

    fun getUserdetails() {
        if (madminUserLocation == null) {
            madminUserLocation = Admin_User_Location()

                //this getting wrong
                mRef.reference.child("Admin Users").child(adminName).setValue( adminName)
                getLastKnownLocation()


        }
        else
        {
            getLastKnownLocation()
        }
    }



    /*
     ---------------------------it stores the user Location of all the users in another node-----------------------------------------------
      */

    fun saveUserLocation()
    {
        if(madminUserLocation != null)
        {
            var locationRef = mRef.reference.child("Admin Users").child(adminName).child("Location")
            locationRef.setValue(madminUserLocation).addOnCompleteListener(object :
                OnCompleteListener<Void> {
                override fun onComplete(task: Task<Void>) {
                    if(task.isSuccessful)
                    {
                        Toast.makeText(this@Admin_Authentication,"आपका स्थान संग्रहीत किया गया है .....",Toast.LENGTH_LONG).show()
                    }
                }
            })
        }
        else{
            getLocationPermission();
        }
    }


    /*
  ----------------------Getting the last known location of the current device------------------------------------------
   */


    fun getLastKnownLocation(){
        mFusedLocationClient.lastLocation.addOnCompleteListener(object :
            OnCompleteListener<Location> {
            override fun onComplete(task: Task<Location>) {
                if(task.isSuccessful)
                {
                    var location= task.result
                    if(location!=null) {
                        var geoPoint = GeoPoint(location?.latitude!!, location.longitude)
                        if (geoPoint != null)
                            madminUserLocation!!.admin_geo_Point = geoPoint
                        else
                            madminUserLocation = null

                        madminUserLocation!!.timestamp
                        saveUserLocation()

                    }
                    else {
                        Toast.makeText(
                            this@Admin_Authentication,
                            "Location not detected  TRY AGAIN........",
                            Toast.LENGTH_LONG
                        ).show()
                        val intent = Intent(this@Admin_Authentication, LoginActivity::class.java)
                        startActivity(intent)
                    }

                }
            }
        })
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
           // getChatrooms()
            getUserdetails()
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
            GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this@Admin_Authentication)
        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working")
            return true
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it")
            val dialog: Dialog = GoogleApiAvailability.getInstance()
                .getErrorDialog(this@Admin_Authentication, available, constants.ERROR_DIALOG_REQUEST)
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
                   // getChatrooms()
                    getUserdetails()
                } else {
                    getLocationPermission()
                }
            }
        }
    }





}