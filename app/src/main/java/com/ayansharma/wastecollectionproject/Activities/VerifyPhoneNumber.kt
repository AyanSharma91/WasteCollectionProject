package com.ayansharma.wastecollectionproject.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.ayansharma.wastecollectionproject.R
import com.ayansharma.wastecollectionproject.model.CountryData
import kotlinx.android.synthetic.main.activity_verify_phone_number.*

class VerifyPhoneNumber : AppCompatActivity() {


   lateinit var  spinner : Spinner
    lateinit var editText: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_phone_number)
        /*
        -getting all the information from the register activity setting up the proper format of the phone number so
        that it can be sent for verification pass all the remaining data as it is to teh OTP acitivity


         */

        //In case the activiy is not loaded







        spinner= findViewById(R.id.spinnerCountries)
        spinner.setAdapter(object : ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,
            CountryData.countryNames){})
          editText = findViewById(R.id.editTextPhone)

        buttonContinue.setOnClickListener{
            val code = CountryData.countryAreaCodes[spinner.selectedItemPosition]
            val number = editText.text.toString().trim()
            if(number.isEmpty() or ((number.length)<10))
            {
                editText.setError(" Valid Number is required")
                editText.requestFocus()
                return@setOnClickListener
            }


            val phoneNumber = "+"+code+number;
            val name = intent.getStringExtra("name")
            val address  = intent.getStringExtra("address")


            val intent = Intent(this@VerifyPhoneNumber, OTPActivity::class.java)
            intent.putExtra("phoneNumber",phoneNumber)
            intent.putExtra("name2",name)

            startActivity(intent)
        }
    }
}
