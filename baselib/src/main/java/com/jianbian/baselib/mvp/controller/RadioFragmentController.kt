package com.jianbian.baselib.mvp.controller

import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import com.jianbian.baselib.adapter.AppViewPagerAdapter
import com.jianbian.baselib.mvp.impl.RadioImpl

class RadioFragmentController :RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener {
    private var group :RadioGroup?=null
    private var fragments = ArrayList<Fragment>()
    private var manager:FragmentManager?=null
    private var viewPager:ViewPager?=null
    private var listener:RadioImpl?=null
    fun groundBindFragment(listener: RadioImpl,group: RadioGroup,fragmentId: Int,fragments: ArrayList<Fragment>,manager: FragmentManager){
        group.setOnCheckedChangeListener(this)
        this.fragments = fragments
        this.group = group
        this.manager = manager
        this.listener = listener
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

    fun groundBindViewPager(listener: RadioImpl,group: RadioGroup,viewPager: ViewPager,manager: FragmentManager,fragments: ArrayList<Fragment>,titleList:List<String>?=null){
        this.group = group
        this.viewPager = viewPager
        this.fragments = fragments
        this.listener = listener
        group.setOnCheckedChangeListener(this)
        viewPager.adapter = AppViewPagerAdapter(manager,fragments,titleList)
        viewPager.addOnPageChangeListener(this)
    }

    override fun onCheckedChanged(p0: RadioGroup?, position: Int) {
        var checkPosition = -1
        group?.run {
            for (index in 0 until  childCount){
                val view  = getChildAt(index)
                if (view is RadioButton){
                    checkPosition++
                    if (view.isChecked){
                        break
                    }
                }
            }
        }
        if (checkPosition<0){
            return
        }
        val transaction = manager?.beginTransaction()
        for (index in fragments.indices){
            if (index == checkPosition){
                transaction?.show(fragments[index])
            }else{
                transaction?.hide(fragments[index])
            }
        }
        transaction?.commitAllowingStateLoss()
        viewPager?.currentItem = checkPosition
        listener?.onRadioChoseCallBack(position)
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        var checkPosition = -1
        group?.run {
            for (index in 0 until  childCount){
                val view  = getChildAt(index)
                if (view is RadioButton){
                    checkPosition++
                    if (checkPosition == position){
                        if (view is RadioButton){
                            view.isChecked = true
                        }
                    }

                }
            }
        }
        listener?.onRadioChoseCallBack(position)
    }
}