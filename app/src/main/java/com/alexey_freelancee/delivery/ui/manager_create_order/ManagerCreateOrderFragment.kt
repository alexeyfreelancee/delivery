package com.alexey_freelancee.delivery.ui.manager_create_order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.alexey_freelancee.delivery.R
import com.alexey_freelancee.delivery.databinding.ManagerCreateOrderFragmentBinding
import com.alexey_freelancee.delivery.ui.MainActivity
import com.alexey_freelancee.delivery.utils.log
import com.alexey_freelancee.delivery.utils.toast
import org.koin.androidx.viewmodel.ext.android.viewModel


class ManagerCreateOrderFragment : Fragment() {


    private val viewModel by viewModel<ManagerCreateOrderViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ManagerCreateOrderFragmentBinding.inflate(inflater, container, false).apply {
            this.viewmodel = viewModel
            this.lifecycleOwner = viewLifecycleOwner
        }.root
    }

    private fun setupSubOrders(view: View) {
        val adapter = AvailableOrdersListAdapter()
        view.findViewById<RecyclerView>(R.id.pickedOrders).adapter = adapter
        viewModel.subOrdersFull.observe(viewLifecycleOwner, {
            adapter.updateList(it)
        })
    }

    private fun setupAvailableOrders(view: View) {
        viewModel.availableOrders.observe(viewLifecycleOwner, {
            val availableOrders = view.findViewById<Spinner>(R.id.availableOrders)
            val adapter = AvailableOrdersAdapter(requireContext(), it, viewModel)
            availableOrders.adapter = adapter
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).supportActionBar?.show()
        (requireActivity() as MainActivity).supportActionBar?.title = "???????????????? ????????????"

        val estimatedTime =  requireArguments().getString("estimatedTime")
        val estimatedTimeText = requireArguments().getString("estimatedTimeText")
        if(estimatedTime.isNullOrEmpty() or estimatedTimeText.isNullOrEmpty()){
            requireContext().toast("???????????????? ????????")
            findNavController().navigate(R.id.action_managerCreateOrderFragment_to_mainScreenFragment)
        } else{
            viewModel.loadAvailableOrders(estimatedTime!!,estimatedTimeText!!)
        }

        viewModel.toast.observe(viewLifecycleOwner, {
            if(!it.hasBeenHandled){
                requireContext().toast(it.peekContent())
            }

        })

        setupSubOrders(view)
        viewModel.availableOrders.observe(viewLifecycleOwner, {
            setupAvailableOrders(view)
        })

        viewModel.createOrder.observe(viewLifecycleOwner, {
            if (!it.hasBeenHandled) {
                log("create order")
                val result = it.peekContent()
                if (result == "ok") {
                    findNavController().navigate(R.id.action_managerCreateOrderFragment_to_mainScreenFragment)
                } else {
                    requireContext().toast(result)
                }
            }

        })
    }

}


