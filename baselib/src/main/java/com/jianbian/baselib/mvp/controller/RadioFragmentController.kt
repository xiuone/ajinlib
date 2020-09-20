package com.jianbian.baselib.mvp.controller

import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import com.jianbian.baselib.adapter.AppViewPagerAdapter

class RadioFragmentController :RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener {
    private var group :RadioGroup?=null
    private var fragments = ArrayList<Fragment>()
    private var manager:FragmentManager?=null
    private var viewPager:ViewPager?=null
    fun groundBindFragment(group: RadioGroup,fragmentId: Int,fragments: ArrayList<Fragment>,manager: FragmentManager){
        group.setOnCheckedChangeListener(this)
        this.fragments = fragments
        this.group = group
        this.manager = manager
        val transaction = manager.beginTransaction()
        for (index in fragments.indices){
            transaction.add(fragmentId, fragments[index])
            if (index == 1) {
                transaction.show(fragments[index])
            }else{
                transaction.hide(fragments[index])
            }
        }
        transaction.commitAllowingStateLoss()
    }

    fun groundBindViewPager(group: RadioGroup,viewPager: ViewPager,manager: FragmentManager,fragments: ArrayList<Fragment>,titleList:List<String>?=null){
        this.group = group
        this.viewPager = viewPager
        this.fragments = fragments
        viewPager.adapter = AppViewPagerAdapter(manager,fragments,titleList)
        viewPager.addOnPageChangeListener(this)
    }

    override fun onCheckedChanged(p0: RadioGroup?, position: Int) {
        val transaction = manager?.beginTransaction()
        for (index in fragments.indices){
            if (index == position){
                transaction?.show(fragments[index])
            }else{
                transaction?.hide(fragments[index])
            }
        }
        transaction?.commitAllowingStateLoss()
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        group?.run {
            if (position<childCount){
                val view  = getChildAt(position)
                if (view is RadioButton){
                    view.isChecked = true
                }
            }
        }
    }
}