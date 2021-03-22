package com.alexey_freelancee.delivery.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.alexey_freelancee.delivery.R
import com.alexey_freelancee.delivery.databinding.LoginFragmentBinding
import com.alexey_freelancee.delivery.ui.MainActivity
import com.alexey_freelancee.delivery.utils.toast
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {


    private val viewModel by viewModel<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return LoginFragmentBinding.inflate(inflater,container,false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewmodel = viewModel
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).supportActionBar?.hide()
        viewModel.successLogin.observe(viewLifecycleOwner,{ result->
            if(!result.hasBeenHandled){
                if(result.peekContent()){
                    findNavController().navigate(R.id.action_loginFragment_to_mainScreenFragment2)
                } else{
                    requireContext().toast("Ошибка")
                }
            }

        })

        viewModel.doRegistration.observe(viewLifecycleOwner,{result->
            if(!result.hasBeenHandled){
                result.peekContent()
                findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
            }
        })
    }


}