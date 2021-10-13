package com.ayansharma.wastecollectionproject.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class ClusterMarker(
    // required field
    private var position: LatLng,
    // required field
    private var title: String,
    // required field
    private var snippet: String?,
    var iconPicture: Int,
    var userID: String

) :
    ClusterItem {

    fun setPosition(position: LatLng) {
        this.position = position
    }

    fun setTitle(title: String) {
        this.title = title
    }

    fun setSnippet(snippet: String) {
        this.snippet = snippet
    }

    override fun getPosition(): LatLng {
        return position
    }

    override fun getTitle(): String? {
        return title
    }

    override fun getSnippet(): String? {
        return snippet
    }

    override fun toString(): String {
        return "ClusterMarker(position=$position, title='$title', snippet='$snippet', iconPicture=$iconPicture, userID='$userID')"
    }


}