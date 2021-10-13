package com.ayansharma.wastecollectionproject.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ayansharma.wastecollectionproject.R
import com.ayansharma.wastecollectionproject.fragments.ResponseRecyclerAdapter
import com.ayansharma.wastecollectionproject.model.Responses
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ResponseActivity: AppCompatActivity() {

    var responseList = arrayListOf<Responses>()
    var responseListSecond = arrayListOf<Responses>()
    lateinit var recyclerView: RecyclerView
    lateinit var recyclerView2 : RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var layoutManager2: RecyclerView.LayoutManager
    lateinit var progressBar: ProgressBar
    lateinit var progressLayout: RelativeLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_response)
        recyclerView = findViewById(R.id.recycler_view)
        layoutManager = LinearLayoutManager(this)
        layoutManager2=LinearLayoutManager(this)

        recyclerView2 = findViewById(R.id.recycler_view2)

        progressBar = findViewById(R.id.progressBar10)
        progressLayout= findViewById(R.id.progressLayout10)
        progressLayout.visibility= View.VISIBLE



        var ref = FirebaseDatabase.getInstance().reference.child("Response_For_Waste_Collection")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (datasnapshot: DataSnapshot in snapshot.children) {
                    if (datasnapshot.key == intent.getStringExtra("Username")) {
                        for (i in datasnapshot.children) {
                            var timestamp = i.key
                            var respon = i.getValue(String::class.java)
                            var obje = Responses(timestamp!!, respon!!)


                            responseList.add(obje)

                        }





                        progressLayout.visibility=View.GONE
                        var adapter : ResponseRecyclerAdapter = ResponseRecyclerAdapter(this@ResponseActivity, responseList)
                        recyclerView.adapter = adapter
                        recyclerView.layoutManager = layoutManager2


                    }
                    else
                    {
                        progressLayout.visibility=View.GONE

                    }
                }
            }
        })







        var ref2 = FirebaseDatabase.getInstance().reference.child("Response_For_Attendance")
        ref2.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (datasnapshot: DataSnapshot in snapshot.children) {
                    if (datasnapshot.key == intent.getStringExtra("Username")) {
                        for (i in datasnapshot.children) {
                            var timestamp = i.key
                            var respon = i.getValue(String::class.java)
                            var obje = Responses(timestamp!!, respon!!)


                            responseListSecond.add(obje)

                        }





                        progressLayout.visibility=View.GONE
                        var adapter : ResponseRecyclerAdapter = ResponseRecyclerAdapter(this@ResponseActivity, responseListSecond)
                        recyclerView2.adapter = adapter
                        recyclerView2.layoutManager = layoutManager


                    }
                    else
                    {
                        progressLayout.visibility=View.GONE

                    }
                }
            }
        })




    }





}





