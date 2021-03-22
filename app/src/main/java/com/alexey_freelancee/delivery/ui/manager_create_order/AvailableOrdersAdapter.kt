package com.alexey_freelancee.delivery.ui.manager_create_order

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.alexey_freelancee.delivery.R
import com.alexey_freelancee.delivery.data.models.Order
import com.alexey_freelancee.delivery.utils.getDateString
import java.lang.reflect.Method


class AvailableOrdersAdapter(
    context: Context,
    items: List<Order>,
    private val viewModel: ManagerCreateOrderViewModel
) :
    ArrayAdapter<Order>(context, 0, items){
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    override fun getDropDownView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        return initView(position, convertView, parent)
    }

    private fun initView(position: Int, convertView: View?, parent: ViewGroup): View {

       val view = convertView ?: LayoutInflater.from(context).inflate(
           R.layout.available_order_row,
           parent,
           false
       )

        val customerName = view.findViewById<TextView>(R.id.customer_name)
        val estimateTime= view.findViewById<TextView>(R.id.estimate_time)
        val index = view.findViewById<TextView>(R.id.index)
        customerName.text = getItem(position)!!.customerName
        estimateTime.text = getDateString(getItem(position)!!.estimateTime)
        index.text = getItem(position)!!.index.toString()

        view.setOnClickListener {
            viewModel.addSubOrder(getItem(position)!!)
        }
        return view
    }



}
