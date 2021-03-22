package com.alexey_freelancee.delivery.ui.splash_screen

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.alexey_freelancee.delivery.R
import com.alexey_freelancee.delivery.ui.MainActivity
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.concurrent.timerTask

class SplashFragment : Fragment() {
    private val viewModel:SplashViewModel by viewModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.splash_fragment, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).supportActionBar?.hide()
        Handler().postDelayed({
            if(viewModel.checkLogin()){
                findNavController().navigate(R.id.action_splashFragment_to_mainScreenFragment)
            } else{
                findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
            }
        },1000)


    }
}