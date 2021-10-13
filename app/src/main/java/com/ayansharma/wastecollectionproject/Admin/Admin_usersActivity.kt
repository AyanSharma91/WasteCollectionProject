package com.ayansharma.wastecollectionproject.Admin

import android.animation.ObjectAnimator
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ayansharma.wastecollectionproject.R
import com.ayansharma.wastecollectionproject.Util.Constants
import com.ayansharma.wastecollectionproject.Util.MyClusterManagerRenderer
import com.ayansharma.wastecollectionproject.Util.Recycler_Adapter
import com.ayansharma.wastecollectionproject.Util.ViewWeightAnimationWrapper
import com.ayansharma.wastecollectionproject.model.Admin_User_Location
import com.ayansharma.wastecollectionproject.model.ClusterMarker
import com.ayansharma.wastecollectionproject.model.Resources
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.maps.DirectionsApiRequest
import com.google.maps.GeoApiContext
import com.google.maps.PendingResult
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.model.DirectionsResult

/*
Remember whenever your are using singleValueEventListenter the data created after quering database gets automatically destroyed
 */

class Admin_usersActivity : AppCompatActivity(),OnMapReadyCallback,View.OnClickListener

     {



    lateinit var recyclerViewAdmin : RecyclerView
    lateinit var layoutManager : RecyclerView.LayoutManager
    lateinit var recyclerAdapter: Recycler_Adapter
    lateinit var progressBar: ProgressBar
    lateinit var progressLayout: RelativeLayout
    lateinit var  mMapView: MapView
    var constants = Constants()
     lateinit var  mGoogleMap : GoogleMap
    lateinit var mMapBoundary : LatLngBounds
     lateinit var mAdminName : Admin_User_Location
    lateinit var AdminName : Admin_User_Location
    var adminUsers = ArrayList<Admin_User_Location>()

    lateinit var nameIntent : String
    lateinit var dB : FirebaseDatabase
    var TAG = " This is what your wanted"

//Handler and Runnable are responsible for making the requests for every 3 seconds
    private val mHandler: Handler = Handler()
    private var mRunnable: Runnable? = null
    private val LOCATION_UPDATE_INTERVAL = 3000

    private var mClusterManager: ClusterManager<ClusterMarker>? = null
    private var mClusterManagerRenderer: MyClusterManagerRenderer? = null
    private val mClusterMarkers: ArrayList<ClusterMarker> = ArrayList()
    lateinit var mMapContainer : RelativeLayout
    lateinit var animationButton : ImageButton




    private val MAP_LAYOUT_STATE_CONTRACTED = 0
    private val MAP_LAYOUT_STATE_EXPANDED = 1
    private var mMapLayoutState = 0


         //Objects for the Goole directions Api
          var mGeoApiContext : GeoApiContext?= null
         //initialized in the initGoogleMapMethod()





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.users_list)
        dB= FirebaseDatabase.getInstance()



  nameIntent = intent.getStringExtra("NameOfTheAdmin")

        //shared Preference to get the admin name in the Admin Service Class
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = sharedPref.edit()
        editor.putString("userName", nameIntent)
        editor.apply()

        recyclerViewAdmin = findViewById(R.id.recyclerViewAdmin)
        layoutManager= LinearLayoutManager(this)
        progressBar = findViewById(R.id.progressBar)
        progressLayout= findViewById(R.id.progressLayout)
        progressLayout.visibility= View.VISIBLE
        mMapView = findViewById(R.id.user_list_map)
        mMapContainer= findViewById(R.id.map_container)

        animationButton = findViewById(R.id.btn_full_screen_map)

        //This is important as we have set this whenever this id is clicked it will get the on Click method
        animationButton.setOnClickListener(this)







        // this functions stores all the users in the list to store it in the recycler view------------------------------------------
        var bookMyList = arrayListOf<Resources>()
        FirebaseDatabase.getInstance()
            .reference
            .child("users")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (dataSnap in dataSnapshot.children) {
                            try {
                                val name: String =
                                    dataSnap.child("name").getValue(String::class.java)!!
                                val phoneNUm =
                                    dataSnap.child("phone_number").getValue(Long::class.java)
                                val address = dataSnap.child("address").getValue(String::class.java)
                                val resours: Resources = Resources(name, phoneNUm!!, address!!)
                                bookMyList.add(resours)
                            }catch (e : NullPointerException)
                            {}




                    }

                    progressLayout.visibility=View.GONE
                    recyclerAdapter= Recycler_Adapter(this@Admin_usersActivity, bookMyList)
                    recyclerViewAdmin.adapter=recyclerAdapter
                    recyclerViewAdmin.layoutManager=layoutManager
                }

                override fun onCancelled(databaseError: DatabaseError) {}

            })



        initGoogleMap(savedInstanceState)
        startLocationService()


    }




         /*
         -------------function to get the directions for the directions api library---------------------------------------------------
          */


         private fun calculateDirections(marker: Marker) {


             var mref = FirebaseDatabase.getInstance().reference.child("Admin Users")
             mref.addListenerForSingleValueEvent(object  : ValueEventListener {
                 override fun onCancelled(error: DatabaseError) {

                 }

                 override fun onDataChange(snapshot: DataSnapshot) {
                       var latitud : Double?
                       var longitud : Double?
                     for(datasnapshot : DataSnapshot in snapshot.children)
                     {
                         if(datasnapshot.key==nameIntent)
                         {

                              latitud = datasnapshot.child("Location").child("admin_geo_Point").child("latitude").getValue(Double::class.java)
                              longitud = datasnapshot.child("Location").child("admin_geo_Point").child("longitude").getValue(Double::class.java)
                             Log.d(TAG, "calculateDirections: calculating directions.")
                             val destination = com.google.maps.model.LatLng(
                                 marker.position.latitude,
                                 marker.position.longitude
                             )
                             val directions = DirectionsApiRequest(mGeoApiContext)
                             directions.alternatives(true)
                               directions.origin(
                                 com.google.maps.model.LatLng(
                                     latitud!!,
                                     longitud!!
                                 )
                             )
                             Log.d(TAG, "original coordinates $latitud , $longitud")
                             Log.d(TAG, "calculateDirections: destination: $destination")
                             directions.destination(destination)
                                 .setCallback(object : PendingResult.Callback<DirectionsResult?> {
                                     override fun onResult(result: DirectionsResult?) {
                                         Log.d(
                                             TAG,
                                             "calculateDirections: routes: " + result!!.routes.get(0).toString()
                                         )
                                         Log.d(
                                             TAG,
                                             "calculateDirections: duration: " + result.routes.get(0).legs.get(0).duration
                                         )
                                         Log.d(
                                             TAG,
                                             "calculateDirections: distance: " + result.routes.get(0).legs.get(0).distance
                                         )
                                         Log.d(
                                             TAG,
                                             "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints.get(
                                                 0
                                             ).toString()
                                         )
                                     }

                                     override fun onFailure(e: Throwable) {
                                         Log.e(
                                             TAG,
                                             "calculateDirections: Failed to get directions: " + e.message
                                         )
                                     }


                                 })


                         }


                     }

                 }
             })


         }

    /*
    ------------------------function to enable service changed coordintes to the map --------------------------------------------------
     */


    private fun startUserLocationsRunnable() {
        Log.d(
            TAG,
            "startUserLocationsRunnable: starting runnable for retrieving updated locations."
        )
        mHandler.postDelayed(Runnable {
            retrieveUserLocations()
            mHandler.postDelayed(mRunnable, LOCATION_UPDATE_INTERVAL.toLong())
        }.also { mRunnable = it }, LOCATION_UPDATE_INTERVAL.toLong())
    }

    private fun stopLocationUpdates() {
        mHandler.removeCallbacks(mRunnable)
    }

    private fun retrieveUserLocations() {
        Log.d(
            TAG,
            "retrieveUserLocations: retrieving location of all users in the chatroom."
        )
        try {
            for (clusterMarker in mClusterMarkers) {
                var ref = FirebaseDatabase.getInstance().reference.child("users")
                ref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {}
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (dataSnapshot: DataSnapshot in snapshot.children) {
                            var ide = dataSnapshot.key
                            //updating the Location
                            for (i in mClusterMarkers.indices) {
                                try {

                                    if (mClusterMarkers.get(i).userID == ide) {
                                        var updateLatLng: LatLng = LatLng(
                                            dataSnapshot.child("latitude")
                                                .getValue(Double::class.java)!!,
                                            dataSnapshot.child("longitude")
                                                .getValue(Double::class.java)!!
                                        )
                                        mClusterMarkers.get(i).position = updateLatLng
                                        mClusterManagerRenderer!!.setUpdateMarker(mClusterMarkers.get(i))
                                    }
                                } catch (e: NullPointerException) {
                                }
                            }
                        }
                    }
                })
            }
        }catch (e : IllegalStateException)
        {

        }
    }













/*
-------------------------This map marks all the users in the map----------------------------------------------------------------------
 */

    private fun addMarkers() {
        if (mGoogleMap != null) {
            if (mClusterManager == null) {
                mClusterManager =
                    ClusterManager(this@Admin_usersActivity.applicationContext, mGoogleMap)
            }
            if (mClusterManagerRenderer == null) {
                mClusterManagerRenderer = MyClusterManagerRenderer(
                    this@Admin_usersActivity,
                    mGoogleMap,
                    mClusterManager
                )
                mClusterManager!!.renderer = mClusterManagerRenderer
            }


            var mRef = FirebaseDatabase.getInstance().reference.child("users")
            mRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    var c=0;
                    for (dataSnapshot: DataSnapshot in snapshot.children) {
                        var name = dataSnapshot.child("name").getValue(String::class.java)
                        var latitude = dataSnapshot.child("latitude").getValue(Double::class.java)
                        var lonitude = dataSnapshot.child("longitude").getValue(Double::class.java)
                        var id = dataSnapshot.child("userID").getValue(String::class.java)
                            //var snippet = ""


                        try {

                            c++;
                            var newClusterMarker = ClusterMarker(
                                LatLng(latitude!!, lonitude!!),
                                name!!,null, R.drawable.waste, id!!
                            )



                            mClusterManager!!.addItem(newClusterMarker)

                            mClusterMarkers.add(newClusterMarker)
                          //  Toast.makeText(this@Admin_usersActivity,""+mClusterMarkers,Toast.LENGTH_LONG).show()
                        } catch (e: NullPointerException) {
                            Log.e(TAG, "addMapMarkers: NullPointerException: " + e.message)
                        }
                    }


                    mClusterManager!!.cluster()
                    setCameraView()
                }
            })

        }

    }





/*
--------------------------function to update Location in the map at a regular interval-----------------------------------------
 */



              /*
              ---------------------this function updates the location in the database
               */
    private fun startLocationService() {
        if (!isLocationServiceRunning()) {
            val serviceIntent = Intent(this, LocationServiceAdmin::class.java)
            //        this.startService(serviceIntent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }
        }
    }

    private fun isLocationServiceRunning(): Boolean {
        val manager: ActivityManager =
            getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if ("com.ayansharma.wastecollectionproject.Admin.LocationServiceAdmin" == service.service.getClassName()) {
                Log.d(
                    TAG,
                    "isLocationServiceRunning: location service is already running."
                )
                return true
            }
        }
        Log.d(TAG, "isLocationServiceRunning: location service is not running.")
        return false
    }







    //Find out why we cannot access it later




    fun setCameraView()
    {

        //THE OTHER METHOD WAS NOT WORKING SO I ADOPTED IT
       var ref = dB.reference.child("Admin Users")
          ref.addValueEventListener(object : ValueEventListener{
              override fun onCancelled(error: DatabaseError) {

              }

              override fun onDataChange(snapshot: DataSnapshot) {
                  for (dataSnap in snapshot.children) {

                      if (dataSnap.key == nameIntent) {
                          var latitude: Double =
                              dataSnap.child("Location").child("admin_geo_Point").child("latitude")
                                  .getValue(Double::class.java)!!
                          var longitude: Double =
                              dataSnap.child("Location").child("admin_geo_Point").child("longitude")
                                  .getValue(Double::class.java)!!

                          var bottomBoundary =latitude -.1
                          var leftBoundary = longitude  -.1
                          var topBoundary = latitude +.1
                          var rightBoundary  = longitude +.1





                          mMapBoundary = LatLngBounds(
                              LatLng(bottomBoundary,leftBoundary),
                              LatLng(topBoundary,rightBoundary))
                          mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary,0))
                      }

                      //Overall Map view window 0.2*0.2 =0.04
                  }

              }
              })
    }


    fun initGoogleMap(savedInstanceState: Bundle?)
    {

        // *** IMPORTANT ***
        // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
        // objects or sub-Bundles.
        var  mapViewBundle : Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(constants.MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this@Admin_usersActivity)

        if(mGeoApiContext==null)
        {
            mGeoApiContext= GeoApiContext.Builder().apiKey(getString(R.string.MAPS_API)).build()
        }

    }

    @Override
    public override fun onSaveInstanceState(outState : Bundle) {
        super.onSaveInstanceState(outState);

        var mapViewBundle : Bundle? = outState.getBundle(constants.MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle =  Bundle()
            outState.putBundle(constants.MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public override fun onResume() {
        super.onResume();
        mMapView.onResume();
        startUserLocationsRunnable()
    }

    @Override
    public override fun onStart() {
        super.onStart();
        mMapView.onStart();

    }

    @Override
    public override fun onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public override fun onMapReady(map: GoogleMap) {
       mGoogleMap=map

        map.isMyLocationEnabled=true
        addMarkers()

        /*
        -----AT FIRST IT WAS NOT WORKING THEN I REPLACED THE LIBRARY WITH ONE USED IN THE COURSE THEN IT WORKED FINE FOR ME-------
         */
      // mGoogleMap.setOnInfoWindowClickListener(this)





    }

    @Override
    public override fun  onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public override fun onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
        stopLocationUpdates()

    }

    @Override
    public override fun onLowMemory() {

        mMapView.onLowMemory()
        super.onLowMemory()
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_full_screen_map -> {
                if (mMapLayoutState == MAP_LAYOUT_STATE_CONTRACTED) {
                    mMapLayoutState = MAP_LAYOUT_STATE_EXPANDED
                    expandMapAnimation()
                } else if (mMapLayoutState == MAP_LAYOUT_STATE_EXPANDED) {
                    mMapLayoutState = MAP_LAYOUT_STATE_CONTRACTED
                    contractMapAnimation()
                }
            }
        }
    }


    private fun expandMapAnimation() {
        val mapAnimationWrapper =
            ViewWeightAnimationWrapper(mMapContainer)
        val mapAnimation: ObjectAnimator = ObjectAnimator.ofFloat(
            mapAnimationWrapper,
            "weight",
            40f,
            100f
        )
        mapAnimation.setDuration(900)
        val recyclerAnimationWrapper =
            ViewWeightAnimationWrapper(recyclerViewAdmin)
        val recyclerAnimation: ObjectAnimator = ObjectAnimator.ofFloat(
            recyclerAnimationWrapper,
            "weight",
            60f,
            0f
        )
        recyclerAnimation.setDuration(900)
        recyclerAnimation.start()
        mapAnimation.start()
    }

    private fun contractMapAnimation() {
        val mapAnimationWrapper =
            ViewWeightAnimationWrapper(mMapContainer)
        val mapAnimation: ObjectAnimator = ObjectAnimator.ofFloat(
            mapAnimationWrapper,
            "weight",
            100f,
            40f
        )
        mapAnimation.setDuration(900)
        val recyclerAnimationWrapper =
            ViewWeightAnimationWrapper(recyclerViewAdmin)
        val recyclerAnimation: ObjectAnimator = ObjectAnimator.ofFloat(
            recyclerAnimationWrapper,
            "weight",
            0f,
            60f
        )
        recyclerAnimation.setDuration(900)
        recyclerAnimation.start()
        mapAnimation.start()
    }






     }








