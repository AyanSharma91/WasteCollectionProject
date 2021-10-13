package com.ayansharma.wastecollectionproject.fragments

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ayansharma.wastecollectionproject.R
import com.ayansharma.wastecollectionproject.model.Responses

class ResponseRecyclerAdapter(val context: Context, val arr: ArrayList<Responses>) : RecyclerView.Adapter<ResponseRecyclerAdapter.DashboardViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.response_reccycler_single_row, parent, false)
        return DashboardViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return arr.size
    }


    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val book = arr[position]


        holder.time.text = book.time
        holder.response.text = book.response


    }

    class DashboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var time = view.findViewById<TextView>(R.id.time)
        var response = view.findViewById<TextView>(R.id.response)


    }
}



