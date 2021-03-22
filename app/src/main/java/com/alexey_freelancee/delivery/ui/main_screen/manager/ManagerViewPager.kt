package com.alexey_freelancee.delivery.ui.main_screen.manager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.alexey_freelancee.delivery.ui.main_screen.manager.FreshManagerFragment
import com.alexey_freelancee.delivery.ui.main_screen.manager.HistoryManagerFragment

class ManagerViewPager(
    fragmentManager: FragmentManager
) : FragmentStatePagerAdapter(fragmentManager){
    override fun getCount() = 2

    override fun getItem(position: Int): Fragment {
        return if (position == 0){
            FreshManagerFragment()
        }else{
            HistoryManagerFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        return  if (position == 0){
            "Новые"
        }else{
            "История"
        }
    }
}