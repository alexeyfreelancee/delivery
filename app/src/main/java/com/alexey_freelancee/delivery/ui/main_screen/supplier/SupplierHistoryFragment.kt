package com.alexey_freelancee.delivery.ui.main_screen.supplier

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.alexey_freelancee.delivery.R
import com.alexey_freelancee.delivery.ui.main_screen.MainScreenViewModel
import com.alexey_freelancee.delivery.ui.main_screen.manager.ManagerOrdersListAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class SupplierHistoryFragment : Fragment() {

    private var adapter : ManagerOrdersListAdapter? = null
    private val viewModel by viewModel<MainScreenViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = ManagerOrdersListAdapter(findNavController(),viewModel)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_supplier_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<RecyclerView>(R.id.supplierHistoryList).adapter = adapter

        viewModel.supplierOrdersHistory.observe(viewLifecycleOwner,{
            adapter?.updateList(it)
        })
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.updateSupplierHistory()
        }

    }


}

