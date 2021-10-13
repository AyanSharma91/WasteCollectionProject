package com.ayansharma.wastecollectionproject.Activities

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.ayansharma.wastecollectionproject.R

class NegativeResponse : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_negative_response)

        //IT is set to handle the event after a cetain time period

//        val handler = Handler()
//
//        handler.postDelayed(Runnable { finishAffinity() }, 1000*5)
    }


    override fun onBackPressed() {

        finishAffinity()
    }
}
