package com.example.btapp.ui.planTrip

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.btapp.R

class NearestStopsAdapter(private val stopsList: MutableList<String>) : RecyclerView.Adapter<NearestStopsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stopName: TextView = itemView.findViewById(R.id.stop_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stop, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.stopName.text = stopsList[position]
    }

    override fun getItemCount(): Int = stopsList.size

    // Helper function to update data in the adapter
    @SuppressLint("NotifyDataSetChanged")
    fun updateStops(newStops: List<String>) {
        stopsList.clear()
        stopsList.addAll(newStops)
        notifyDataSetChanged()
    }
}
