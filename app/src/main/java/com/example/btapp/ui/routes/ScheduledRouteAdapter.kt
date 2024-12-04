package com.example.btapp.ui.routes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.btapp.databinding.FragmentPlanTripBinding

class ScheduledRouteAdapter(private val routeNames: List<String>) : RecyclerView.Adapter<ScheduledRouteAdapter.RouteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val binding = FragmentPlanTripBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RouteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        val routeName = routeNames[position]
        holder.bind(routeName)
    }

    override fun getItemCount(): Int {
        return routeNames.size
    }

    inner class RouteViewHolder(private val binding: FragmentPlanTripBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(routeName: String) {
            //binding.routeNameText.text = routeName
        }
    }
}
