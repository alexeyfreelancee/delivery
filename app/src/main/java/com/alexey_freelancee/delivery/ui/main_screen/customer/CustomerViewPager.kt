package com.alexey_freelancee.delivery.ui.main_screen.customer

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.alexey_freelancee.delivery.ui.main_screen.customer.FreshCustomerFragment
import com.alexey_freelancee.delivery.ui.main_screen.customer.HistoryCustomerFragment


class CustomerViewPager(
    fragmentManager: FragmentManager

) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getCount() = 2


    override fun getItem(i: Int): Fragment {

        return if (i == 0) {
            FreshCustomerFragment()
        } else {
            HistoryCustomerFragment()
        }

    }

    override fun getPageTitle(position: Int): CharSequence {

        return if (position == 0) {
            "В работе"
        } else {
            "История"
        }
    }
}