package com.alexey_freelancee.delivery.ui.main_screen.customer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.alexey_freelancee.delivery.data.models.Order
import com.alexey_freelancee.delivery.databinding.OrderRowBinding

class OrderListAdapter() :RecyclerView.Adapter<OrderListAdapter.OrderViewHolder>() {
    private val items = ArrayList<Order>()

    fun updateList(newList:List<Order>){

        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(order: Order){

            DataBindingUtil.bind<OrderRowBinding>(itemView)?.apply {

                this.order = order
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = OrderRowBinding.inflate(inflater,parent,false)
        return OrderViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}