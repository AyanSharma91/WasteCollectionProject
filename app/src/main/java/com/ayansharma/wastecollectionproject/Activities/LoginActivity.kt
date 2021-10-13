package com.ayansharma.wastecollectionproject.Activities

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ayansharma.wastecollectionproject.Admin.Admin_Authentication
import com.ayansharma.wastecollectionproject.Admin.Admin_usersActivity
import com.ayansharma.wastecollectionproject.R
import com.ayansharma.wastecollectionproject.Util.Constants
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*



@Suppress("DEPRECATION")
class LoginActivity: AppCompatActivity() {

        lateinit var progressBar : ProgressBar
        lateinit var name2: String


    var constants= Constants()
    var mLocationPermissionGranted=false
    var TAG= "This is what you wanted"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if(checkMapServices())
            if(mLocationPermissionGranted)
            {



            }
            else
            {   getLocationPermission()

            }

           progressBar= findViewById(R.id.progressBar)
        admin.setOnClickListener{
            val intent = Intent(this@LoginActivity,Admin_Authentication::class.java)
            startActivity(intent)
        }
         progressBar.visibility= View.GONE

        btn_login.setOnClickListener{

             name2= name.text.toString()


            if((name2 == "")) {
                Toast.makeText(this@LoginActivity,"सभी फ़ील्ड दर्ज करें", Toast.LENGTH_LONG).show()


            }

            else {

                            progressBar.visibility = View.VISIBLE
                            val intent = Intent(this@LoginActivity, VerifyPhoneNumber::class.java)
                            intent.putExtra("name", name2)

                            startActivity(intent)
                            progressBar.visibility = View.GONE


                    }}





    }

    override fun onStart() {
        super.onStart()
        if(FirebaseAuth.getInstance().currentUser!=null)
        {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            intent.putExtra("FLAG-2","login")
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
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
            // getChatrooms()


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
            GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this@LoginActivity)
        if (available == ConnectionResult.SUCCESS) {

            Log.d(TAG, "isServicesOK: Google Play Services is working")
            return true
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {

            Log.d(TAG, "isServicesOK: an error occured but we can fix it")
            val dialog: Dialog = GoogleApiAvailability.getInstance()
                .getErrorDialog(this@LoginActivity, available, constants.ERROR_DIALOG_REQUEST)
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


                } else {
                    getLocationPermission()
                }
            }
        }
    }

}







