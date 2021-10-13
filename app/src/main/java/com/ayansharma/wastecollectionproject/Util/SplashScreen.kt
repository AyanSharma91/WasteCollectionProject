package com.ayansharma.wastecollectionproject.Util

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.ayansharma.wastecollectionproject.Activities.LoginActivity
import com.ayansharma.wastecollectionproject.R
import java.lang.Exception


class SplashScreen: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen)

        val background = object : Thread() {
            override fun run() {
                try {
                    Thread.sleep(4000)
                    val intent= Intent(this@SplashScreen,
                        LoginActivity::class.java)
                    startActivity(intent)

                }   catch (e: Exception){
                    e.printStackTrace()}
            }

        }
        background.start()
    }
}
