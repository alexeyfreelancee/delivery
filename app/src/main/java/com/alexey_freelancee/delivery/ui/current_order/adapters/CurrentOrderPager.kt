package com.alexey_freelancee.delivery.ui.current_order.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.alexey_freelancee.delivery.ui.current_order.pages.OrderInfoFragment
import com.alexey_freelancee.delivery.ui.current_order.pages.RouteFragment

class CurrentOrderPager(
    fragmentManager: FragmentManager


) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getCount() = 2


    override fun getItem(i: Int): Fragment {

        return if (i == 0) {
            RouteFragment()
        } else {
            OrderInfoFragment()
        }

    }

    override fun getPageTitle(position: Int): CharSequence {

        return if (position == 0) {
            "Маршрут"
        } else {
            "Данные по заказу"
        }
    }
}