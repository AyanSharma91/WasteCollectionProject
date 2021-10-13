package com.ayansharma.wastecollectionproject.model

import android.provider.ContactsContract

class User {

    lateinit var userID: String
    lateinit var name: String
    var phone_number: Long = 0
    lateinit var address : String
    var photo : String ?=null


    constructor() {}


    constructor(
        userID : String,
        name: String,
        phone_number: Long,
        address : String,
        photo: String


    ) {
        this.userID= userID
        this.name = name
        this.phone_number = phone_number
        this.address=address
        this.photo=photo
    }

    override fun toString(): String {
        return "User(userID='$userID', name='$name', phone_number=$phone_number, address='$address', photo=$photo)"
    }


}


