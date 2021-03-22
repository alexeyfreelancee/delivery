package com.alexey_freelancee.delivery.ui.manager_create_order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.alexey_freelancee.delivery.R
import com.alexey_freelancee.delivery.databinding.ManagerCreateOrderFragmentBinding
import com.alexey_freelancee.delivery.ui.MainActivity
import com.alexey_freelancee.delivery.utils.toast
import org.koin.androidx.viewmodel.ext.android.viewModel


class ManagerCreateOrderFragment : Fragment() {



    private  val viewModel by viewModel<ManagerCreateOrderViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ManagerCreateOrderFragmentBinding.inflate(inflater, container, false).apply {
            this.viewmodel = viewModel
            this.lifecycleOwner = viewLifecycleOwner
        }.root
    }

    private fun setupSubOrders(view:View){
        val adapter = AvailableOrdersListAdapter()
        view.findViewById<RecyclerView>(R.id.pickedOrders).adapter = adapter
        viewModel.subOrdersFull.observe(viewLifecycleOwner,{
            adapter.updateList(it)
        })
    }
    private fun setupAvailableOrders(view: View){
        viewModel.availableOrders.observe(viewLifecycleOwner,{
            val availableOrders = view.findViewById<Spinner>(R.id.availableOrders)
            val adapter = AvailableOrdersAdapter(requireContext(),it,viewModel)
            availableOrders.adapter = adapter
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).supportActionBar?.show()
        (requireActivity() as MainActivity).supportActionBar?.title = "Создание заказа"
        viewModel.loadAvailableOrders(requireArguments().getString("estimatedTime")!!, requireArguments().getString("estimatedTimeText")!!)
        viewModel.toast.observe(viewLifecycleOwner, {
            requireContext().toast(it.peekContent())
        })

        setupSubOrders(view)
        viewModel.availableOrders.observe(viewLifecycleOwner, {
            setupAvailableOrders(view)
        })

        viewModel.createOrder.observe(viewLifecycleOwner, {
            if (it.peekContent()) {
                findNavController().navigate(R.id.action_managerCreateOrderFragment_to_mainScreenFragment)
            } else {
                requireContext().toast("Ошибка")
            }
        })
    }

}


