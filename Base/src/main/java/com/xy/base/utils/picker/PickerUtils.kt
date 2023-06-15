package com.xy.base.utils.picker

import android.app.Activity
import android.content.Context
import com.github.gzuliyujiang.wheelpicker.DatePicker
import com.github.gzuliyujiang.wheelpicker.annotation.DateMode
import com.github.gzuliyujiang.wheelpicker.contract.OnDatePickedListener
import com.github.gzuliyujiang.wheelpicker.entity.DateEntity
import com.xy.base.R
import com.xy.base.utils.exp.getResString
import com.xy.base.utils.config.language.LanguageManger
import java.util.*

fun Activity.showBirthDayPicker(oldDatePicker:DatePicker?,pickerListener: OnDatePickedListener,dayMode: DayMode?=null):DatePicker{
    return showBirthDayPicker(oldDatePicker, pickerListener, dayMode,
        0XFF999999.toInt(), 0XFF222222.toInt(), 0XFF333333.toInt())
}



fun Activity.showBirthDayPicker(oldDatePicker:DatePicker?, pickerListener: OnDatePickedListener, dayMode: DayMode?=null,
                                textColor:Int, selectTextColor:Int, labelColor:Int):DatePicker{
    oldDatePicker?.dismiss()
    val yearStr = getResString(R.string.year)
    val monthStr = getResString(R.string.month)
    val dayStr = getResString(R.string.day)
    val calender = Calendar.getInstance()


    val currentDay = calender.get(Calendar.DAY_OF_MONTH)
    val currentMonth = calender.get(Calendar.MONTH)+1
    val currentYear = calender.get(Calendar.YEAR)


    val picker = DatePicker(this)
    val wheelLayout = picker.wheelLayout
    wheelLayout.setDateMode(DateMode.YEAR_MONTH_DAY)
    wheelLayout.setDateLabel(yearStr, monthStr, dayStr)
//        wheelLayout.setDateFormatter(UnitDateFormatter());
    val defaultEntity = DateEntity.target(dayMode?.year?:currentYear, dayMode?.month?:currentMonth, dayMode?.day?:currentDay)
    val endDateEntity = DateEntity.target(currentYear, currentMonth, currentDay)
    val startDateEntity = DateEntity.target(currentYear-150, 1, 1)
    wheelLayout.setRange(startDateEntity, endDateEntity, DateEntity.today())
    wheelLayout.setDefaultValue(defaultEntity)

    wheelLayout.setCurtainEnabled(false)
//        wheelLayout.setCurtainColor(0xFFCC0000)
    wheelLayout.setIndicatorEnabled(false)
//        wheelLayout.setIndicatorColor(0xFFFF0000);
//        wheelLayout.setIndicatorSize(view.getResources().getDisplayMetrics().density * 2);
    wheelLayout.setTextColor(textColor)
    wheelLayout.setSelectedTextColor(selectTextColor)
    wheelLayout.yearLabelView.setTextColor(labelColor)
    wheelLayout.monthLabelView.setTextColor(labelColor)
    wheelLayout.dayLabelView.setTextColor(labelColor)
    wheelLayout.setResetWhenLinkage(false)
    picker.setOnDatePickedListener(pickerListener)
    picker.show()
    return picker
}


fun DayMode.getBirth(context: Context?):String{
    return if (year == 0 && month == 0 && day == 0){
        "0 / 0 / 0"
    } else if (context == null || LanguageManger.instant.isZh(context)){
        "$year / $month / $day"
    }else{
        "${getMonthStr(month)} $day , $year"
    }
}

/**
 * 获取英文的月份缩写
 */
private fun getMonthStr(month: Int):String{
    return when(month){
        1->"Jan"
        2->"Feb"
        3->"Mar"
        4->"Apr"
        5->"May"
        6->"Jun"
        7->"Jul"
        8->"Aug"
        9->"Sept"
        10->"Oct"
        11->"Nov"
        else->"Dec"
    }
}