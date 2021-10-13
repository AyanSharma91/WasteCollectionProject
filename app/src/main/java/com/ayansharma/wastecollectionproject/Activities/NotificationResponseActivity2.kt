package com.ayansharma.wastecollectionproject.Activities

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.ayansharma.wastecollectionproject.R
import com.ayansharma.wastecollectionproject.Util.AlertReceiver
import com.ayansharma.wastecollectionproject.Util.AlertReceiver2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class NotificationResponseActivity2 : AppCompatActivity() {


    lateinit var yes: Button
    lateinit var no: Button
    lateinit var receiver: AlertReceiver2
    lateinit var manager: NotificationManager

    private val CHANNEL_ID2: String? = "1234"
    lateinit var progressBar: ProgressBar
    lateinit var progressLayout: RelativeLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications_response_activity_2)
        receiver = AlertReceiver2()
        progressBar = findViewById(R.id.progressBar6)
        progressLayout= findViewById(R.id.progressLayout6)
        progressLayout.visibility= View.GONE



        yes = findViewById(R.id.Yes)
        no = findViewById(R.id.No)


        manager =
            this@NotificationResponseActivity2.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //Creating the Notification  Channel




        var calender = Calendar.getInstance()
        var simpleDateFormat = SimpleDateFormat("EEEE , dd-MMM-yyy hh:mm::ss a")
        var dateTime = simpleDateFormat.format(calender.time)

        yes.setOnClickListener {



            progressLayout.visibility= View.VISIBLE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                manager.deleteNotificationChannel(CHANNEL_ID2)

            manager.cancel(5)

            var ref = FirebaseDatabase.getInstance().reference.child("users")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (datasnapshot: DataSnapshot in snapshot.children) {
                        if ((datasnapshot.child("userID")
                                .getValue(String::class.java)) == FirebaseAuth.getInstance().currentUser!!.uid
                        ) {
                            var name = datasnapshot.child("name").getValue(String::class.java)
                            FirebaseDatabase.getInstance().reference.child("Response_For_Attendance")
                                .child(name!!).child(dateTime).setValue("Yes")
                            var intent = Intent(
                                this@NotificationResponseActivity2,
                                Notification_Response_Activity::class.java
                            )
                            startActivity(intent)
                            progressLayout.visibility= View.GONE


                        }
                    }
                }
            })
        }

        no.setOnClickListener {
            progressLayout.visibility= View.VISIBLE

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                manager.deleteNotificationChannel(CHANNEL_ID2)

            manager.cancel(5)

            var ref = FirebaseDatabase.getInstance().reference.child("users")
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (datasnapshot: DataSnapshot in snapshot.children) {
                        if ((datasnapshot.child("userID")
                                .getValue(String::class.java)) == FirebaseAuth.getInstance().currentUser!!.uid
                        ) {
                            var name = datasnapshot.child("name").getValue(String::class.java)
                            FirebaseDatabase.getInstance().reference.child("Response_For_Attendance")
                                .child(name!!).child(dateTime).setValue("No")
                            var intent = Intent(
                                this@NotificationResponseActivity2,
                                Notification_Response_Activity::class.java
                            )
                            startActivity(intent)
                            progressLayout.visibility= View.GONE


                        }
                    }
                }
            })
        }


    }


}

