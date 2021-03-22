package com.alexey_freelancee.delivery.ui.current_order.pages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alexey_freelancee.delivery.R
import com.alexey_freelancee.delivery.data.models.Order
import com.alexey_freelancee.delivery.databinding.FragmentRouteBinding
import com.alexey_freelancee.delivery.ui.MainActivity
import com.alexey_freelancee.delivery.ui.current_order.CurrentOrderViewModel
import com.alexey_freelancee.delivery.utils.log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


class RouteFragment : Fragment() {

    private val viewModel by viewModel<CurrentOrderViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return FragmentRouteBinding.inflate(inflater,container,false).apply {
            this.viewmodel = viewModel
            this.lifecycleOwner = viewLifecycleOwner
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.subOrders.observe(viewLifecycleOwner,{
//            val map = (requireActivity() as MainActivity).supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
//            map.getMapAsync(viewModel)
        })
        viewModel.loadOrders()
    }





}