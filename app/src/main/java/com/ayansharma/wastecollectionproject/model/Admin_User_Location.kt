package com.ayansharma.wastecollectionproject.model

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

class Admin_User_Location {

    var admin_name : String?=null
    var admin_geo_Point : GeoPoint?=null
    @ServerTimestamp
    var  timestamp : Date? = null



    constructor()
    {}

    constructor(admin_name: String?, admin_geo_Point: GeoPoint?, timestamp: Date?) {
        this.admin_name = admin_name
        this.admin_geo_Point = admin_geo_Point
        this.timestamp = timestamp
    }

    override fun toString(): String {
        return "Admin_User_Location(admin_name=$admin_name, admin_geo_Point=$admin_geo_Point, timestamp=$timestamp)"
    }
}