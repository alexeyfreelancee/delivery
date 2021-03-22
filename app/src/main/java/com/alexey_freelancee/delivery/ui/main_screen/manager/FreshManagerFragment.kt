package com.alexey_freelancee.delivery.ui.main_screen.manager

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alexey_freelancee.delivery.R
import com.alexey_freelancee.delivery.ui.main_screen.MainScreenViewModel
import com.alexey_freelancee.delivery.ui.main_screen.customer.OrderListAdapter
import com.alexey_freelancee.delivery.utils.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class FreshManagerFragment : Fragment() {
    private val viewModel by viewModel<MainScreenViewModel>()
    private val adapter = OrderListAdapter()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_fresh_manager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<RecyclerView>(R.id.managerFreshList).adapter = adapter

        viewModel.managerOrdersFresh.observe(viewLifecycleOwner,{
            log("manager fresh = ${it.size}")
            adapter.updateList(it)
        })
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.updateManagerFresh()
        }

    }


}