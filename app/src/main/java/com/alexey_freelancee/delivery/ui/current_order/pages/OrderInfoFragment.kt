package com.alexey_freelancee.delivery.ui.current_order.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.alexey_freelancee.delivery.R
import com.alexey_freelancee.delivery.databinding.FragmentOrderInfoBinding
import com.alexey_freelancee.delivery.ui.current_order.CurrentOrderViewModel
import com.alexey_freelancee.delivery.ui.current_order.adapters.SubOrderInfoListAdapter
import com.alexey_freelancee.delivery.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class OrderInfoFragment : Fragment() {
    private var spinnerInitialized = false
    private var adapter: SubOrderInfoListAdapter? = null
    private val viewModel by viewModel<CurrentOrderViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = SubOrderInfoListAdapter { createTime, status ->
            viewModel.updateSubOrderStatus(createTime, status)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return FragmentOrderInfoBinding.inflate(inflater, container, false).apply {
            this.viewmodel = viewModel
            this.lifecycleOwner = viewLifecycleOwner
        }.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<RecyclerView>(R.id.subOrders).adapter = adapter

        viewModel.order.observe(viewLifecycleOwner, {
            setupSubOrderStatus(view.findViewById(R.id.orderStatus), it.status)
        })
        viewModel.subOrders.observe(viewLifecycleOwner, {
            log("sub orders = ${it.size}")
            adapter?.updateList(it)
        })
        viewModel.loadData()
    }

    private fun setupSubOrderStatus(spinner: Spinner, status: String) {
        CoroutineScope(Dispatchers.Main).launch {
            spinner.isEnabled = isOnline()
        }
        ArrayAdapter.createFromResource(
            spinner.context,
            R.array.order_status,
            R.layout.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        val listener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (!spinnerInitialized) {
                    spinnerInitialized = true
                    return
                }
                when (position) {
                    0 -> {
                        viewModel.updateOrderStatus(STATUS_COMPLETED)
                    }
                    1 -> {
                        viewModel.updateOrderStatus(STATUS_PROCESSING)
                    }
                    2 -> {
                        viewModel.updateOrderStatus(STATUS_PACKED)
                    }
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        spinner.onItemSelectedListener = listener

        when (status) {
            STATUS_PACKED -> spinner.setSelection(2)
            STATUS_PROCESSING -> spinner.setSelection(1)
            STATUS_COMPLETED -> spinner.setSelection(0)
        }
    }

}