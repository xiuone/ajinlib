package com.xy.baselib.widget.tab.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class FragmentChangeManager
    (private val mFragmentManager: FragmentManager,  mContainerViewId: Int, private val mFragments: ArrayList<Fragment>) {
    var currentTab = 0
        private set

    init {
        for (fragment in mFragments) {
            mFragmentManager.beginTransaction().add(mContainerViewId, fragment).hide(fragment).commit()
        }
        setFragments(0)
    }

    /** 界面切换控制  */
    fun setFragments(index: Int) {
        for (i in mFragments.indices) {
            val ft = mFragmentManager.beginTransaction()
            val fragment = mFragments[i]
            if (i == index) {
                ft.show(fragment)
            } else {
                ft.hide(fragment)
            }
            ft.commit()
        }
        currentTab = index
    }

    val currentFragment: Fragment
        get() = mFragments[currentTab]


}