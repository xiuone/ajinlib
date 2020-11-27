package com.jianbian.baselib.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.jianbian.baselib.R
import com.jianbian.baselib.adapter.BaseRecyclerAdapter
import com.jianbian.baselib.adapter.DateAdapter
import com.jianbian.baselib.mvp.impl.OnItemClickListener
import com.jianbian.baselib.mvp.mode.DateMode
import com.jianbian.baselib.utils.AppUtil
import com.jianbian.baselib.utils.NestScrollGridLayoutManager
import com.jianbian.baselib.utils.setOnClick
import kotlinx.android.synthetic.main.date_view.view.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * 年月日筛选
 */
class DateView(context: Context, attrs: AttributeSet?=null):FrameLayout(context,attrs),OnItemClickListener,View.OnClickListener{
    private var adpater: DateAdapter ?=null
    private var year:Int = 0
    private var month :Int = 0
    init {
        LayoutInflater.from(context).inflate(R.layout.date_view,this)
        date_recyclerView.layoutManager = NestScrollGridLayoutManager(context,7,false)


        val array = context.obtainStyledAttributes(attrs, R.styleable.DateView)

        val selectBackground = array.getResourceId(R.styleable.DateView_date_select_background,R.drawable.bg_black_0000_corners_30)
        val selectBackgroundNot = array.getResourceId(R.styleable.DateView_date_select_background_not,R.drawable.bg_transparent)
        val toDayBackGround = array.getResourceId(R.styleable.DateView_date_toDay_background,R.drawable.bg_red_e32c_corners_30)
        val selectTextColor = array.getColor(R.styleable.DateView_date_select_textColor,AppUtil.getColor(context,R.color.white))
        val selectTextColorNot = array.getColor(R.styleable.DateView_date_select_textColor_not,AppUtil.getColor(context,R.color.white))
        val toDayTextColor = array.getColor(R.styleable.DateView_date_toDay_textColor,AppUtil.getColor(context,R.color.gray_9999))
        val commonTextColor = array.getColor(R.styleable.DateView_date_common_textColor,AppUtil.getColor(context,R.color.gray_3333))

        adpater = DateAdapter(selectBackground,selectBackgroundNot,toDayBackGround,selectTextColor,selectTextColorNot,toDayTextColor,commonTextColor)
        date_recyclerView.adapter = adpater
        adpater?.onItemClickListener=this

        val cal = Calendar.getInstance()
        val year = cal[Calendar.YEAR]
        val month = cal[Calendar.MONTH] + 1
        setNewData(year,month)

        date_right_button.setOnClick(this)
        date_left_button.setOnClick(this)
    }

    override fun onItemClick(adapter: BaseRecyclerAdapter<*>, view: View, position: Int) {
        val data = adapter.getItem(position)
        if (data != null && data is DateMode) {
            if (!data.isRightMore && !data.isLeftMore) {
                setSeletDay(data.day)
            } else {
                jumpMonth(year, month, data.isLeftMore)
            }
        }
    }

    /**
     * 设置当月选中
     */
    private fun setSeletDay(seletDay:Int){
        if (adpater == null)return
        for (index in adpater!!.data.indices){
            var item = adpater!!.data[index]
            if (!item.isLeftMore && !item.isRightMore){
                if (item.day == seletDay && !item.isSelect){
                    adpater!!.data[index].isSelect = true
                    adpater!!.notifyItemChanged(index)
                }else if (item.day != seletDay && item.isSelect){
                    adpater!!.data[index].isSelect = false
                    adpater!!.notifyItemChanged(index)
                }
            }
        }
    }

    /**
     * 跳动月份
     */
    private fun jumpMonth(nowYear:Int,nowMoth:Int,isLeft:Boolean){
        val selectDate = getJumpDate(nowYear,nowMoth,isLeft)
        setNewData(selectDate[0],selectDate[1])
    }
    
    private fun setNewData(year: Int,month: Int){
        val maxDay =getMaxDay(year,month)
        val minWeek = getDayWeek(year,month,1)
        val maxWeek = getDayWeek(year,month,maxDay)

        val leftDate = getJumpDate(year,month,true)
        val leftMaxDay = getMaxDay(leftDate[0],leftDate[1])

        val nweData = ArrayList<DateMode>()
        for (index in(leftMaxDay-minWeek+2) ..leftMaxDay){
            val item = DateMode(
                year, month, index
                , false, true, false, false
            )
            adpater?.notifychangeItem(nweData.size,item)
            nweData.add(item)
        }
        for (index in 1 .. maxDay){
            val cal = Calendar.getInstance()
            val nowYear = cal[Calendar.YEAR]
            val nowMonth = cal[Calendar.MONTH] + 1
            val nowDay = cal[Calendar.DAY_OF_MONTH]
            val item = DateMode(
                year, month, index
                , (nowYear == year && nowMonth == month && nowDay == index), false, false, nowDay == index
            )
            adpater?.notifychangeItem(nweData.size,item)
            nweData.add(item)
        }
        for (index in 1..(7-maxWeek)){
            val item = DateMode(
                year, month, index,
                false, false, true, false
            )
            adpater?.notifychangeItem(nweData.size,item)
            nweData.add(item)
        }
        date_title.text = "${year}年${if (month<10) "0$month" else "$month"}月"
        this.year = year
        this.month = month
        if (adpater?.data?.size?:0 <= 0)
            adpater?.setNewData(nweData)
    }

    /**
     * 获取跳转的年月日
     */
    private fun getJumpDate(nowYear: Int,nowMoth: Int,isLeft: Boolean):ArrayList<Int>{
        var seletMoth = nowMoth
        var seletYear = nowYear
        if (isLeft && nowMoth > 1){
            seletMoth = nowMoth -1
            seletYear = nowYear
        }else if (isLeft && nowMoth <= 1){
            seletMoth = 12
            seletYear = nowYear-1
        }else if (!isLeft && nowMoth>=12){
            seletYear = nowYear+1
            seletMoth = 1
        }else if (!isLeft && nowMoth<12){
            seletYear = nowYear
            seletMoth = nowMoth +1
        }
        val data = ArrayList<Int>()
        data.add(seletYear)
        data.add(seletMoth)
        return data
    }


    /**
     * 获取某年某月最大天数
     */
    private fun getMaxDay(year:Int,month:Int):Int{
        val cal: Calendar = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month+1)
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    /**
     * 获取当前年月日的天数
     */
    private fun getDayWeek(year: Int,month: Int,day:Int):Int{
        val cal: Calendar = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month+1)
        cal.set(Calendar.DAY_OF_MONTH, day)
        return cal.get(Calendar.DAY_OF_WEEK) +1
    }

    override fun onClick(view: View) {
        when(view.id){
            R.id.date_left_button->{
                jumpMonth(year,month,true)
            }
            R.id.date_right_button->{
                jumpMonth(year,month,false)
            }
        }
    }

    fun getSelectDate(): DateMode?{
        if (adpater == null)return null
        for (index in adpater!!.data.indices){
            if ( adpater!!.data[index].isSelect)
                return adpater!!.data[index]
        }
        return null
    }
}