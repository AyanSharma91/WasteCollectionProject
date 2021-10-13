package com.ayansharma.wastecollectionproject.Admin

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ServerTimestamp
import com.ayansharma.wastecollectionproject.model.User
import java.util.*


class User_Location_Admin  {

      var geo_Point : GeoPoint?=null
     //its function is that if we insert a null in the field it will automatically insert the time



    constructor(){}

    constructor(geo_Point: GeoPoint, timestamp: Date, user: User) {
        this.geo_Point = geo_Point

    }

    override fun toString(): String {
        return "User_Location_Admin(geo_Point=$geo_Point)"
    }


}


