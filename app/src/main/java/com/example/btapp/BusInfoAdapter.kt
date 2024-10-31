package com.example.btapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// delete this, same as RouteAdapter





//class BusInfoAdapter(private val busInfoList: List<BusInfo>) : RecyclerView.Adapter<BusInfoAdapter.BusInfoViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusInfoViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bus_info, parent, false)
//        return BusInfoViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: BusInfoViewHolder, position: Int) {
//        val busInfo = busInfoList[position]
//        holder.bind(busInfo)
//    }
//
//    override fun getItemCount(): Int = busInfoList.size
//
//    class BusInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val busNumber: TextView = itemView.findViewById(R.id.text_bus_number)
//        private val busLocation: TextView = itemView.findViewById(R.id.text_bus_location)
//
//        fun bind(busInfo: BusInfo) {
//            busNumber.text = "Bus: ${busInfo.VehicleID}"
//            busLocation.text = "Location: Lat: ${busInfo.Latitude}, Lon: ${busInfo.Longitude}"
//        }
//    }
//}
