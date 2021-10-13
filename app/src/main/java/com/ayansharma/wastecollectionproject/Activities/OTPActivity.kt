package com.ayansharma.wastecollectionproject.Activities

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
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
import com.ayansharma.wastecollectionproject.Admin.User_Location_Admin
import com.ayansharma.wastecollectionproject.R
import com.ayansharma.wastecollectionproject.Util.Constants
import com.ayansharma.wastecollectionproject.model.Resources
import com.ayansharma.wastecollectionproject.model.User
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.synthetic.main.activity_o_t_p.*
import java.util.concurrent.TimeUnit



class OTPActivity : AppCompatActivity() {

    var verificationID: String = ""
    lateinit var mAuth: FirebaseAuth
    lateinit var userID: String
    var mFirebaseDatabase : FirebaseDatabase= FirebaseDatabase.getInstance()
    var mref : DatabaseReference= mFirebaseDatabase.getReference()
    lateinit var progressBar : ProgressBar
    lateinit var editText: EditText
     lateinit var etrname: String

    lateinit var phonenumber :String


    var constants= Constants()
    var mLocationPermissionGranted=false
    var TAG= "This is what you wanted"







    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_o_t_p)
        mAuth = FirebaseAuth.getInstance()
        editText = findViewById(R.id.editTextCode)
        progressBar = findViewById(R.id.progressbar)


        if(checkMapServices())
            if(mLocationPermissionGranted)
            {
                // getChatrooms()


            }
            else
            {   getLocationPermission()

            }



        etrname = intent.getStringExtra("name2")

        phonenumber = intent.getStringExtra("phoneNumber")


        sendVerificationCode(phonenumber)






        buttonSignIn.setOnClickListener {

            val code= editText.text.toString().trim()
            if(code.isEmpty() || (code.length<6))
            {
                editText.setError("Enter Code...........")
                editText.requestFocus()
                return@setOnClickListener
            }

            verifyCode(code)
        }



    }













    /*
    ------------------This is to verify if it is not automatically done by the system---------------------------------------
     */

    fun verifyCode(code: String) {
        var credential = PhoneAuthProvider.getCredential(verificationID, code)
        signInWithCredential(credential)
    }

    fun signInWithCredential(credential: PhoneAuthCredential) {

        mAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                //Here we will create the user and enter its credentials to the database
                addNewUser(etrname,phonenumber.toLong(),"")



                val intent = Intent(this@OTPActivity,
                    MainActivity::class.java)
                intent.putExtra("FLAG-1","otp")
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)



            } else {
                Toast.makeText(this@OTPActivity, "Authentication Failed......", Toast.LENGTH_SHORT).show()
            }
        }
    }




    /**
     * --------------------------------------------------------Firebase to add new User ----------------------------------------------------------------------------
     */

    fun addNewUser(name :String, contact : Long, address : String){
        try{
            userID = mAuth.currentUser!!.getUid()
        }catch(e: java.lang.NullPointerException){
            Toast.makeText(this@OTPActivity,"haaaaaaaaaaaaaaaaaaaaa",Toast.LENGTH_LONG).show()
        }
        userID = mAuth.currentUser!!.getUid()
        val user = User(
            userID,
            name,
            contact,
            address,"0"
        )


        mref.child("users")
            .child(userID)
            .setValue(user)


        /**
         * Add information to the users nodes
         * Add information to the user_account_settings node
         * @param email
         * @param username
         * @param description
         * @param website
         * @param profile_photo
         */

    }



    fun sendVerificationCode(number: String) {

        progressBar.visibility= View.VISIBLE
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            number, 60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, mCallBack
        )
    }

    private val mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                verificationID = p0
            }

            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                var code = p0.smsCode
                if(code!=null)
                {
                    editText.setText(code)
                    verifyCode(code)

                }
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Toast.makeText(this@OTPActivity, p0.message, Toast.LENGTH_SHORT).show()
            }

        }


    override fun onStart() {
        super.onStart()
        if(FirebaseAuth.getInstance().currentUser != null)
        {
            val intent = Intent(this@OTPActivity,
                MainActivity::class.java)
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
            GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this@OTPActivity)
        if (available == ConnectionResult.SUCCESS) {

            Log.d(TAG, "isServicesOK: Google Play Services is working")
            return true
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {

            Log.d(TAG, "isServicesOK: an error occured but we can fix it")
            val dialog: Dialog = GoogleApiAvailability.getInstance()
                .getErrorDialog(this@OTPActivity, available, constants.ERROR_DIALOG_REQUEST)
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

                } else {
                    getLocationPermission()
                }
            }
        }
    }
















}




