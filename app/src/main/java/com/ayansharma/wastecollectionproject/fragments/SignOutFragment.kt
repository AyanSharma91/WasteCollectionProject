package com.ayansharma.wastecollectionproject.fragments

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.fragment.app.Fragment

import com.ayansharma.wastecollectionproject.Activities.LoginActivity
import com.ayansharma.wastecollectionproject.R
import com.ayansharma.wastecollectionproject.Util.AlertReceiver
import com.ayansharma.wastecollectionproject.Util.AlertReceiver2
import com.google.firebase.auth.FirebaseAuth


class SignOutFragment : Fragment() {


    lateinit var signOUT : Button
    lateinit var progressBar3 : ProgressBar
    lateinit var manager: NotificationManager
    private val name: CharSequence? = "ayan"
    private val CHANNEL_ID: String? = "123"
    private val CHANNEL_ID2: String? = "1234"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         var view= inflater.inflate(R.layout.fragment_sign_out, container, false)
        progressBar3 = view.findViewById(R.id.progressBar3) as ProgressBar
        progressBar3.visibility= View.GONE
        manager =
            (activity as Context).getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var alarmManager: AlarmManager =
            (activity as Context).getSystemService(Context.ALARM_SERVICE) as AlarmManager

        signOUT= view.findViewById(R.id.SignOUT) as Button
        signOUT.setOnClickListener{
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
               manager.deleteNotificationChannel(CHANNEL_ID)
               manager.deleteNotificationChannel(CHANNEL_ID2)
           }

            manager.cancel(1)
            manager.cancel(5)
            var intent2 = Intent(activity as Context, AlertReceiver::class.java)
            var pendingIntent = PendingIntent.getBroadcast(activity, 2, intent2, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager.cancel(pendingIntent)


            var alarmManagerr : AlarmManager = (activity as Context).getSystemService(Context.ALARM_SERVICE) as AlarmManager
            var intent3 = Intent(activity as Context, AlertReceiver::class.java)
            var  pendingIntent2 = PendingIntent.getBroadcast(activity as Context, 1 , intent3,PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManagerr.cancel(pendingIntent2)


            var alarmManagerr8 : AlarmManager = (activity as Context).getSystemService(Context.ALARM_SERVICE) as AlarmManager
            var intent4 = Intent(activity as Context, AlertReceiver2::class.java)
            var  pendingIntent6 = PendingIntent.getBroadcast(activity as Context, 15 , intent4,PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManagerr8.cancel(pendingIntent6)


            var alarmManagerr9 : AlarmManager = (activity as Context).getSystemService(Context.ALARM_SERVICE) as AlarmManager
            var intent5 = Intent(activity as Context, AlertReceiver2::class.java)
            var  pendingIntent3 = PendingIntent.getBroadcast(activity as Context, 90 , intent5,PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManagerr9.cancel(pendingIntent3)

            progressBar3.visibility= View.VISIBLE
           var mAuth= FirebaseAuth.getInstance()
            mAuth.signOut()
            val intent = Intent(activity as Context, LoginActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)

        }


        return view
    }

}
