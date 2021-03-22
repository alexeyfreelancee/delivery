package com.alexey_freelancee.delivery.ui.main_screen.manager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.alexey_freelancee.delivery.R
import com.alexey_freelancee.delivery.data.models.ManagerOrder
import com.alexey_freelancee.delivery.databinding.ManagerOrderRowBinding
import com.alexey_freelancee.delivery.ui.main_screen.MainScreenViewModel
import com.alexey_freelancee.delivery.utils.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ManagerOrdersListAdapter(
    private val navController: NavController,
    private val viewModel:MainScreenViewModel
) : RecyclerView.Adapter<ManagerOrdersListAdapter.ManagerViewHolder>(){
    private val items = ArrayList<ManagerOrder>()
     fun updateList(newList:List<ManagerOrder>){
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }
    inner class ManagerViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView){
        fun bind(order:ManagerOrder){

            val binding = DataBindingUtil.bind<ManagerOrderRowBinding>(itemView)?.apply {
                this.order = order
                this.dataLoading = true
            }
            itemView.setOnClickListener {
                val bundle = Bundle().apply {
                    putLong("create_time", order.createTime)
                }
                navController.navigate(R.id.action_global_orderInfo, bundle)
            }

            val adapter  = ManagerSubOrderListAdapter(order.createTime, navController)
            binding?.subOrders?.adapter = adapter
            CoroutineScope(Dispatchers.IO).launch {
                val orders = viewModel.loadSubOrders(order.subOrders)
                withContext(Dispatchers.Main){
                    adapter.updateList(orders)
                    binding?.dataLoading = false
                }
            }




        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManagerViewHolder {
       val inflater = LayoutInflater.from(parent.context)
        val binding = ManagerOrderRowBinding.inflate(inflater,parent,false)
        return ManagerViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ManagerViewHolder, position: Int) {
      holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}