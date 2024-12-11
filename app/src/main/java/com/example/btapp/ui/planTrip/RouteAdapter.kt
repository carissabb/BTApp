package com.example.btapp.ui.planTrip

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.btapp.R

class RoutesAdapter(private val routes: MutableList<String>) :
    RecyclerView.Adapter<RoutesAdapter.RouteViewHolder>() {

    class RouteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val routeNameText: TextView = itemView.findViewById(R.id.route_name_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_route, parent, false)
        return RouteViewHolder(view)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        val route = routes[position]
        holder.routeNameText.text = route
    }

    override fun getItemCount(): Int = routes.size

    fun updateRoutes(newRoutes: List<String>) {
        routes.clear()
        routes.addAll(newRoutes)
        //notifyDataSetChanged()
    }
}