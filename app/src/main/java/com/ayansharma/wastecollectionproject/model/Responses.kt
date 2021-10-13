package com.ayansharma.wastecollectionproject.model

class Responses {



    lateinit var time : String
    lateinit var response : String


    constructor() {}
    constructor(time: String, response: String) {
        this.time = time
        this.response= response
    }

    override fun toString(): String {
        return "Responses(time='$time', response='$response')"
    }


}





