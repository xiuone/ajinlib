package com.xy.base.act

import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.viewpager.widget.ViewPager
import com.xy.base.ViewPagerChangeListener

abstract class ActivityBaseViewPager : ActivityBaseSwipeBack() ,ViewPager.OnPageChangeListener{


    protected fun bindViewPageGroup(viewPager: ViewPager?,group:RadioGroup?){
        viewPager?.addOnPageChangeListener(object : ViewPagerChangeListener(){
            override fun onPageSelected(position: Int) {
                val groupView = group?:return
                var checkPosition = 0
                for (index in 0 until groupView.childCount) {
                    val view = groupView.getChildAt(index)
                    if (view is RadioButton) {
                        if (checkPosition == position){
                            view.isChecked = true
                            return
                        }
                        checkPosition++
                    }
                }
            }
        })
        group?.setOnCheckedChangeListener { group, _ ->
            var checkPosition = 0
            for (index in 0 until group.childCount) {
                val view = group.getChildAt(index)
                if (view is RadioButton) {
                    checkPosition++
                    if (view.isChecked) {
                        break
                    }
                }
            }
            viewPager?.currentItem = checkPosition
        }
    }

    protected fun bindViewPage(viewPager: ViewPager?){
        viewPager?.addOnPageChangeListener(this)
    }



    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {}

    override fun onPageScrollStateChanged(state: Int) {}
}