package com.alexey_freelancee.delivery.ui.manager_create_order

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alexey_freelancee.delivery.R
import com.alexey_freelancee.delivery.data.models.Order
import com.alexey_freelancee.delivery.utils.getDateString

class AvailableOrdersListAdapter : RecyclerView.Adapter<AvailableOrdersListAdapter.AvailableViewHolder>() {
    private val items = ArrayList<Order>()

    fun updateList(newList:List<Order>){
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }
    inner class AvailableViewHolder(itemView:View): RecyclerView.ViewHolder(itemView){
        fun bind(order: Order){
            val customerName = itemView.findViewById<TextView>(R.id.customer_name)
            val estimateTime= itemView.findViewById<TextView>(R.id.estimate_time)
            val index = itemView.findViewById<TextView>(R.id.index)
            customerName.text = order.customerName
            estimateTime.text = getDateString(order.estimateTime)
            index.text = order.index.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailableViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.available_order_row, parent, false)
        return AvailableViewHolder(view)
    }

    override fun onBindViewHolder(holder: AvailableViewHolder, position: Int) {
     holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}