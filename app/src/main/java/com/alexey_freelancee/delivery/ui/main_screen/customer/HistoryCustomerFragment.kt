package com.alexey_freelancee.delivery.ui.main_screen.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.alexey_freelancee.delivery.R
import com.alexey_freelancee.delivery.ui.main_screen.MainScreenViewModel
import com.alexey_freelancee.delivery.utils.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class HistoryCustomerFragment : Fragment() {
    private var adapter= OrderListAdapter()
    private val viewModel by viewModel<MainScreenViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_history_orders, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       requireView().findViewById<RecyclerView>(R.id.history_list).adapter = adapter

        viewModel.customerOrdersHistory.observe(viewLifecycleOwner,{
            log("customer history = ${it.size }")
            adapter.updateList(it)
        })
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.updateCustomerHistory()
        }

    }


}