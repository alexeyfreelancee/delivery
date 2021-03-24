package com.alexey_freelancee.delivery.ui.registration

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.navigation.fragment.findNavController
import com.alexey_freelancee.delivery.R
import com.alexey_freelancee.delivery.databinding.RegistrationFragmentBinding
import com.alexey_freelancee.delivery.ui.MainActivity
import com.alexey_freelancee.delivery.utils.TYPE_CUSTOMER
import com.alexey_freelancee.delivery.utils.TYPE_MANAGER
import com.alexey_freelancee.delivery.utils.TYPE_SUPPLIER
import com.alexey_freelancee.delivery.utils.toast
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegistrationFragment : Fragment() {


    private val viewModel by viewModel<RegistrationViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return RegistrationFragmentBinding.inflate(inflater,container,false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewmodel = viewModel
        }.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).supportActionBar?.show()
        (requireActivity() as MainActivity).supportActionBar?.title = "Регистрация"

        setupUserType(view)
        viewModel.doRegistration.observe(viewLifecycleOwner, {
            if(!it.hasBeenHandled){
                val result = it.peekContent()
                if(result == "ok"){

                    findNavController().navigate(R.id.action_registrationFragment_to_mainScreenFragment2)
                }else{
                    requireContext().toast(result)
                }
            }
        })


    }

    private fun setupUserType(view:View){
        val spinner = view.findViewById<Spinner>(R.id.user_type)
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.user_type,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        val listener = object:AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(spinner.getItemAtPosition(position).toString().toLowerCase()){
                    "заказчик"->{
                        viewModel.type.postValue(TYPE_CUSTOMER)
                    }
                    "менеджер"->{
                        viewModel.type.postValue(TYPE_MANAGER)
                    }
                    "поставщик"->{
                        viewModel.type.postValue(TYPE_SUPPLIER)
                    }
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinner.onItemSelectedListener = listener
    }
}