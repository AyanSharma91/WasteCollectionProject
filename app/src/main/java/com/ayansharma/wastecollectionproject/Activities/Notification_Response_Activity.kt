package com.ayansharma.wastecollectionproject.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ayansharma.wastecollectionproject.R

class Notification_Response_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification__response_)
    }


    override fun onBackPressed() {
        super.onBackPressed()

        finishAffinity()
    }
}