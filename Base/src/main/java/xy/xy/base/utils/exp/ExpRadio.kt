package xy.xy.base.utils.exp

import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.viewpager.widget.ViewPager
import xy.xy.base.widget.viewpager.AppViewPagerChangeListener
import xy.xy.base.widget.viewpager.ViewPagerListenerImpl

fun RadioGroup?.bindViewPager(viewPager: ViewPager?){
    if (this == null || viewPager == null) return
    this.setOnCheckedChangeListener(object :RadioGroup.OnCheckedChangeListener{
        override fun onCheckedChanged(p0: RadioGroup?, p1: Int) {
            val selectIndex = getRadioButtonSelectIndex()?:return
            viewPager.currentItem = selectIndex
        }
    })
    ViewPagerListenerImpl(viewPager, object :AppViewPagerChangeListener{
        override fun onPageSelected(position: Int) {
            getRadioButton(position)?.isSelected = true
        }
    })
}



fun RadioGroup.getRadioButton(selectIndex:Int):RadioButton?{
    var currentRadioIndex = -1
    for (index in 0 until childCount){
        val childView = getChildAt(index)
        if (childView is RadioButton){
            currentRadioIndex ++
            if (selectIndex == currentRadioIndex){
                return childView
            }
        }
    }
    return null
}

fun RadioGroup.getRadioButtonSelectIndex():Int?{
    var currentRadioIndex = -1
    for (index in 0 until childCount){
        val childView = getChildAt(index)
        if (childView is RadioButton && childView.isChecked){
            currentRadioIndex ++
            return currentRadioIndex
        }
    }
    return null
}