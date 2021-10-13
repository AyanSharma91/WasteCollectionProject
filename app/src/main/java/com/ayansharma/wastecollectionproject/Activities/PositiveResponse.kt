package com.ayansharma.wastecollectionproject.Activities

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.ayansharma.wastecollectionproject.R

class PositiveResponse : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_positive_response)

//        val handler = Handler()
//
//        handler.postDelayed(Runnable { finishAffinity() }, 1000 *2)
    }

    override fun onBackPressed() {

        finishAffinity()
    }
}
