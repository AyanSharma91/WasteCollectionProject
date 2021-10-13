package com.ayansharma.wastecollectionproject.Util

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Handler
import android.view.View
import androidx.core.app.NotificationCompat
import com.ayansharma.wastecollectionproject.Activities.NotificationResponseActivity
import com.ayansharma.wastecollectionproject.Activities.NotificationResponseActivity2
import com.ayansharma.wastecollectionproject.R


//2nd alarm
private val name2 : CharSequence?="ayan2"
private val CHANNEL_ID2 : String?="1234"


class AlertReceiver2: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {



        sendNotifications2(context,intent)

    }
 lateinit  var manager : NotificationManager

       //Second Notifications


    fun sendNotifications2(context: Context?,intent: Intent?){

        manager = context!!.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        //Creating the Notification  Channel

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) {

            var importance = NotificationManager.IMPORTANCE_DEFAULT
            var channel2: NotificationChannel = NotificationChannel(CHANNEL_ID2,name2,importance)
            channel2.description="DESCRIPTION"
            channel2.enableVibration(true)
            channel2.enableLights(true)
            channel2.setLightColor(R.color.colorPrimary)
            channel2.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            manager.createNotificationChannel(channel2)
            val handler = Handler()

            // handler.postDelayed(Runnable { manager.deleteNotificationChannel(CHANNEL_ID)  }, 1000*5)

        }



        var intent = Intent(context, NotificationResponseActivity2::class.java)
        var viewPendingIntent = PendingIntent.getActivity(context, 5, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        //Notification Creation
        val notification = NotificationCompat.Builder(context,"1234")
            .setContentTitle("Waste Collection")
            .setContentText(
                "जवाब देने के लिए कृपया अधिसूचना पर क्लिक करें ...\n" +
                        "आपके सहयोग के लिए धन्यवाद।")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSmallIcon(R.drawable.star)
            .setColor(255)
            .setStyle(object :NotificationCompat.BigPictureStyle(){}
                .bigLargeIcon(null))
            .setLargeIcon(BitmapFactory.decodeResource(context.resources,R.drawable.waste))
            .setStyle(object : NotificationCompat.BigTextStyle(){}
                .bigText(
                    "जवाब देने के लिए कृपया अधिसूचना पर क्लिक करें ...\n" +
                            "आपके सहयोग के लिए धन्यवाद।"))

            //.addAction(R.drawable.ic_launcher_foreground , "यहाँ क्लिक करें" , viewPendingIntent)
            .setContentIntent(viewPendingIntent)
            .setAutoCancel(true)


        manager.notify(5,notification.build())
        val handler = Handler()

        //  handler.postDelayed(Runnable { manager.cancel(1) }, 1000*2)



    }




}