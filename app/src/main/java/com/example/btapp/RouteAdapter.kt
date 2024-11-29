// RouteAdapter.kt
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.btapp.CurrentRoutesResponse
import com.example.btapp.R

class RouteAdapter(
    private val routes: List<CurrentRoutesResponse>,
    private val onRouteClick: (CurrentRoutesResponse) -> Unit // for on-click listener
) : RecyclerView.Adapter<RouteAdapter.RouteViewHolder>() {

    inner class RouteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val routeNameTextView: TextView = itemView.findViewById(R.id.route_name_text)

        // get route short names and append color
        fun bind(route: CurrentRoutesResponse) {
            routeNameTextView.text = route.routeShortName
            val routeHexColor = "#${route.routeColor}" //append # for processing
            routeNameTextView.setTextColor(Color.parseColor(routeHexColor))
            itemView.setOnClickListener { onRouteClick(route) } // set on-click listener
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_route, parent, false)
        return RouteViewHolder(view)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        holder.bind(routes[position])
    }

    override fun getItemCount() = routes.size
}
