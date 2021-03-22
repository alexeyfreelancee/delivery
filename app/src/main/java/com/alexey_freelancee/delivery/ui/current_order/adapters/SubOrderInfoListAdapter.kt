package com.alexey_freelancee.delivery.ui.current_order.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.alexey_freelancee.delivery.R
import com.alexey_freelancee.delivery.data.models.Order
import com.alexey_freelancee.delivery.databinding.SubOrderRowInfoBinding
import com.alexey_freelancee.delivery.utils.STATUS_COMPLETED

import com.alexey_freelancee.delivery.utils.STATUS_PACKED
import com.alexey_freelancee.delivery.utils.STATUS_PROCESSING

class SubOrderInfoListAdapter(private val updateStatus: (createTime:Long,status:String) -> Unit) : RecyclerView.Adapter<SubOrderInfoListAdapter.SubOrderInfoViewHolder>() {
    private val items = ArrayList<Order>()
    private var spinnerInitialized = false
    fun updateList(newList:List<Order>){
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }
    inner class SubOrderInfoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(order: Order){
            val binding = DataBindingUtil.bind<SubOrderRowInfoBinding>(itemView)?.apply {
                this.order = order
            }
            if(binding?.subOrderStatus!=null){

                setupSubOrderStatus(binding.subOrderStatus, order)
                when(order.status){
                    STATUS_PACKED -> binding.subOrderStatus.setSelection(2)
                    STATUS_PROCESSING -> binding.subOrderStatus.setSelection(1)
                    STATUS_COMPLETED -> binding.subOrderStatus.setSelection(0)
                }
            }

        }
    }

    private fun setupSubOrderStatus(spinner: Spinner, order: Order){
        ArrayAdapter.createFromResource(
            spinner.context,
            R.array.order_status,
            R.layout.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        val listener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(!spinnerInitialized){
                    spinnerInitialized = true
                    return
                }
                when (position) {
                    0 -> {
                        updateStatus.invoke(order.createTime, STATUS_COMPLETED)
                    }
                    1 -> {
                        updateStatus.invoke(order.createTime, STATUS_PROCESSING)
                    }
                    2 -> {
                        updateStatus.invoke(order.createTime, STATUS_PACKED)
                    }
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }
        spinner.onItemSelectedListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubOrderInfoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SubOrderRowInfoBinding.inflate(inflater,parent,false)
        return SubOrderInfoViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: SubOrderInfoViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount()=items.size
}