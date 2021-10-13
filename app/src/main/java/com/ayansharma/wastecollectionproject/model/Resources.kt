package com.ayansharma.wastecollectionproject.model

import android.R
import java.math.BigDecimal

class Resources {


    lateinit var name: String
    var phone_number : Long =0L
    lateinit var address : String


    constructor() {}


    constructor(

        name: String,
        phone_number: Long,
        address : String

    ) {

        this.name = name
        this.phone_number = phone_number
        this.address=address
    }

    override fun toString(): String {
        return "Resources(name='$name', phone_number=$phone_number, address='$address')"
    }


}


