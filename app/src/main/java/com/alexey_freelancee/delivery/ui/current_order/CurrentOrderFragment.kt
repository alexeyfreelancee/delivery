package com.alexey_freelancee.delivery.ui.current_order

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.alexey_freelancee.delivery.R

import com.alexey_freelancee.delivery.databinding.CurrentOrderFragmentBinding
import com.alexey_freelancee.delivery.ui.MainActivity
import com.alexey_freelancee.delivery.ui.current_order.adapters.CurrentOrderPager
import com.alexey_freelancee.delivery.utils.log
import com.google.android.material.tabs.TabLayout

import org.koin.androidx.viewmodel.ext.android.viewModel

var createTime = 0L
class CurrentOrderFragment : Fragment() {

    private val viewModel by viewModel<CurrentOrderViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createTime =  arguments?.getLong("create_time")!!
        log("init create time ${arguments?.getLong("create_time")!!}")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return CurrentOrderFragmentBinding.inflate(inflater,container,false).apply {
            this.viewmodel = viewModel
            this.lifecycleOwner = viewLifecycleOwner
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).supportActionBar?.hide()
        setupViewPager(view)
    }
    private fun setupViewPager(view:View){
        val pager= view.findViewById<ViewPager>(R.id.currentOrderPager)
        val tabs = view.findViewById<TabLayout>(R.id.currentOrderTabs)
        pager.adapter = CurrentOrderPager(requireFragmentManager())
        tabs.setupWithViewPager(pager)
    }


}