package com.ayansharma.wastecollectionproject.Util

import android.content.Context
import android.content.Intent
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.ayansharma.wastecollectionproject.Activities.ResponseActivity
import com.ayansharma.wastecollectionproject.Admin.Admin_usersActivity
import com.ayansharma.wastecollectionproject.R
import com.ayansharma.wastecollectionproject.model.Resources




class Recycler_Adapter(val context: Context, val arr: ArrayList<Resources>) : RecyclerView.Adapter<Recycler_Adapter.DashboardViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_single_row, parent, false)
        return DashboardViewHolder(
            view
        )
    }

    override fun getItemCount(): Int {
        return arr.size
    }


    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val book = arr[position]

        holder.txtBookAuthor.text = book.address
        holder.txtBookName.text = book.name
        holder.txtBookPrice.text = book.phone_number.toString()
        holder.parentLayout.setOnClickListener{
            var intent= Intent(context,ResponseActivity::class.java)
            intent.putExtra("Username", book.name)
            context.startActivity(intent)
        }


    }

    class DashboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtBookName = view.findViewById<TextView>(R.id.bookName)
        val txtBookAuthor = view.findViewById<TextView>(R.id.bookAuthor)
        val txtBookPrice = view.findViewById<TextView>(R.id.phone_number)
        val parentLayout= view.findViewById<CardView>(R.id.parentLayout)


    }
}



