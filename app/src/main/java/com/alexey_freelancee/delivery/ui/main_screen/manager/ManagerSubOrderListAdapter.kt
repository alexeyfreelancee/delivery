package com.alexey_freelancee.delivery.ui.main_screen.manager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.alexey_freelancee.delivery.R
import com.alexey_freelancee.delivery.data.models.Order
import com.alexey_freelancee.delivery.databinding.SubOrderRowBinding

class ManagerSubOrderListAdapter(
    private val createTime:Long,
    private val navController: NavController
) : RecyclerView.Adapter<ManagerSubOrderListAdapter.SubOrderViewHolder>() {
    private val items = ArrayList<Order>()
    fun updateList(newList:List<Order>){
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }
    inner class SubOrderViewHolder(itemView:View): RecyclerView.ViewHolder(itemView){
        fun bind(order:Order){
            DataBindingUtil.bind<SubOrderRowBinding>(itemView)?.apply {
                this.order = order
            }
            itemView.setOnClickListener {
                val bundle = Bundle().apply {
                    putLong("create_time", createTime)
                }
                navController.navigate(R.id.action_global_orderInfo, bundle)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubOrderViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SubOrderRowBinding.inflate(inflater,parent,false)
        return SubOrderViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: SubOrderViewHolder, position: Int) {
      holder.bind(items[position])
    }

    override fun getItemCount()=items.size
}