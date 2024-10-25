import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.btapp.BusRoute
import com.example.btapp.R

class RouteAdapter(private val routes: MutableList<BusRoute>, private val clickListener: (String) -> Unit) : RecyclerView.Adapter<RouteAdapter.RouteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_route, parent, false)
        return RouteViewHolder(view)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        val route = routes[position]
        holder.bind(route, clickListener)
    }

    override fun getItemCount(): Int {
        return routes.size
    }

    fun updateRoutes(newRoutes: List<BusRoute>) {
        routes.clear()
        routes.addAll(newRoutes)
        notifyDataSetChanged()
    }

    class RouteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(route: BusRoute, clickListener: (String) -> Unit) {
            itemView.setOnClickListener { clickListener(route.id) } // Use the id from BusRoute
        }
    }
}
