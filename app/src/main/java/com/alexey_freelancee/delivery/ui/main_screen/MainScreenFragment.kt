package com.alexey_freelancee.delivery.ui.main_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import com.alexey_freelancee.delivery.R
import com.alexey_freelancee.delivery.databinding.MainScreenFragmentBinding
import com.alexey_freelancee.delivery.ui.MainActivity
import com.alexey_freelancee.delivery.ui.main_screen.customer.CustomerViewPager
import com.alexey_freelancee.delivery.ui.main_screen.manager.ManagerViewPager
import com.alexey_freelancee.delivery.ui.main_screen.supplier.SupplierViewPager
import com.alexey_freelancee.delivery.utils.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainScreenFragment : Fragment() {

    private var previousManagerPosition = 0
    private var previousCustomerPosition = 0
    private var previousSupplierPosition = 0

    private val viewModel by viewModel<MainScreenViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        return MainScreenFragmentBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewmodel = viewModel
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).supportActionBar?.show()
        (requireActivity() as MainActivity).supportActionBar?.elevation = 0f
        (requireActivity() as MainActivity).supportActionBar?.title = "Заказы"
        view.findViewById<FloatingActionButton>(R.id.add_button).setOnClickListener {
            findNavController().navigate(R.id.action_mainScreenFragment_to_customerCreateOrderFragment)
        }
        view.findViewById<FloatingActionButton>(R.id.manager_add_button).setOnClickListener {
            viewModel.showManagerDatePicker(requireContext())
        }
        view.findViewById<SwipeRefreshLayout>(R.id.swipe_to_refresh).setOnRefreshListener {
            viewModel.updateUserType()
        }

        viewModel.toast.observe(viewLifecycleOwner, {
            if(!it.hasBeenHandled){
                requireContext().toast(it.peekContent())
            }

        })
        viewModel.createManagerOrder.observe(viewLifecycleOwner, {
            if (!it.hasBeenHandled) {
                findNavController().navigate(
                    R.id.action_mainScreenFragment_to_managerCreateOrderFragment,
                    it.peekContent()
                )
            }

        })

        viewModel.userType.observe(viewLifecycleOwner, {
            setupViewPager(view)
            when (it) {
                TYPE_MANAGER -> {
                    log("TYPE_MANAGER")
                    view.findViewById<ConstraintLayout>(R.id.manager).visibility = View.VISIBLE
                    view.findViewById<ConstraintLayout>(R.id.supplier).visibility = View.GONE
                    view.findViewById<ConstraintLayout>(R.id.customer).visibility = View.GONE
                }
                TYPE_SUPPLIER -> {
                    log("TYPE_SUPPLIER")
                    view.findViewById<ConstraintLayout>(R.id.manager).visibility = View.GONE
                    view.findViewById<ConstraintLayout>(R.id.supplier).visibility = View.VISIBLE
                    view.findViewById<ConstraintLayout>(R.id.customer).visibility = View.GONE
                }
                TYPE_CUSTOMER -> {
                    log("TYPE_CUSTOMER")
                    view.findViewById<ConstraintLayout>(R.id.manager).visibility = View.GONE
                    view.findViewById<ConstraintLayout>(R.id.supplier).visibility = View.GONE
                    view.findViewById<ConstraintLayout>(R.id.customer).visibility = View.VISIBLE
                }
            }
        })


        viewModel.updateUserType()


    }


    private fun setupViewPager(view: View) {
        val customerPager = view.findViewById<ViewPager>(R.id.customerPager)
        val customerTabs = view.findViewById<TabLayout>(R.id.customerTabs)
        customerPager.adapter = CustomerViewPager(requireFragmentManager())
        customerTabs.setupWithViewPager(customerPager)
        customerPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                previousCustomerPosition = position
            }

            override fun onPageScrollStateChanged(state: Int) {}

        })
        customerPager.addOnPageChangeListener(object : TabLayout.TabLayoutOnPageChangeListener(
            customerTabs
        ) {
            override fun onPageScrollStateChanged(state: Int) {
                toggleRefreshing(state == ViewPager.SCROLL_STATE_IDLE);
            }
        })





        val managerPager = view.findViewById<ViewPager>(R.id.managerPager)
        val managerTabs = view.findViewById<TabLayout>(R.id.managerTabs)
        managerPager.adapter = ManagerViewPager(requireFragmentManager())
        managerTabs.setupWithViewPager(managerPager)
        managerPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                previousManagerPosition = position
            }

            override fun onPageScrollStateChanged(state: Int) {}

        })
        managerPager.addOnPageChangeListener(object : TabLayout.TabLayoutOnPageChangeListener(
            managerTabs
        ) {
            override fun onPageScrollStateChanged(state: Int) {
                toggleRefreshing(state == ViewPager.SCROLL_STATE_IDLE);
            }
        })





        val supplierPager = view.findViewById<ViewPager>(R.id.supplierPager)
        val supplierTabs = view.findViewById<TabLayout>(R.id.supplierTabs)
        supplierPager.adapter = SupplierViewPager(requireFragmentManager())
        supplierTabs.setupWithViewPager(supplierPager)
        supplierPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                previousSupplierPosition = position
            }

            override fun onPageScrollStateChanged(state: Int) {}

        })
        supplierPager.addOnPageChangeListener(object : TabLayout.TabLayoutOnPageChangeListener(
            supplierTabs
        ) {
            override fun onPageScrollStateChanged(state: Int) {
                toggleRefreshing(state == ViewPager.SCROLL_STATE_IDLE);
            }
        })


        requireView().findViewById<ViewPager>(R.id.customerPager).currentItem =
            previousCustomerPosition
        requireView().findViewById<ViewPager>(R.id.managerPager).currentItem =
            previousManagerPosition
        requireView().findViewById<ViewPager>(R.id.supplierPager).currentItem =
            previousSupplierPosition
    }

    fun toggleRefreshing(enabled: Boolean) {
        if (requireView().findViewById<SwipeRefreshLayout>(R.id.swipe_to_refresh) != null) {
            requireView().findViewById<SwipeRefreshLayout>(R.id.swipe_to_refresh).isEnabled =
                enabled
        }
    }
}