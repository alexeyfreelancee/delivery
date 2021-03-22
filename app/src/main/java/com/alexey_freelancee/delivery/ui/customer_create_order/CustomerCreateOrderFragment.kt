package com.alexey_freelancee.delivery.ui.customer_create_order

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.alexey_freelancee.delivery.R
import com.alexey_freelancee.delivery.databinding.CustomerCreateOrderFragmentBinding
import com.alexey_freelancee.delivery.ui.MainActivity
import com.alexey_freelancee.delivery.utils.toast
import org.koin.androidx.viewmodel.ext.android.viewModel

class CustomerCreateOrderFragment : Fragment() {


    private  val viewModel by viewModel<CustomerCreateOrderViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return CustomerCreateOrderFragmentBinding.inflate(inflater,container,false).apply {
            this.viewmodel = viewModel
            this.lifecycleOwner = viewLifecycleOwner
        }.root
    }

    private fun setupStorageAddress(spinner: Spinner){
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.storage_addresses,
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
                viewModel.storageAddress.postValue(spinner.getItemAtPosition(position).toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) { }
        }
        spinner.onItemSelectedListener = listener
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).supportActionBar?.show()
        (requireActivity() as MainActivity).supportActionBar?.title = "Создание заказа"

        setupStorageAddress(view.findViewById(R.id.storageAddress))
        viewModel.toast.observe(viewLifecycleOwner, Observer {
            requireContext().toast(it.peekContent())
        })


        viewModel.createOrder.observe(viewLifecycleOwner,{
            if(it.peekContent()){
                findNavController().navigate(R.id.action_customerCreateOrderFragment_to_mainScreenFragment)
            }else{
                requireContext().toast("Ошибка")
            }
        })
    }


}