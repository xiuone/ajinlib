package com.xy.base.assembly

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import com.github.gzuliyujiang.wheelview.contract.OnWheelChangedListener
import com.github.gzuliyujiang.wheelview.widget.NumberWheelView
import com.github.gzuliyujiang.wheelview.widget.WheelView
import com.xy.base.assembly.base.BaseAssembly
import com.xy.base.assembly.base.BaseAssemblyView
import com.xy.base.dialog.base.BaseDialog
import com.xy.base.dialog.listener.DialogCancelSureView
import com.xy.base.dialog.listener.DialogImplListener
import com.xy.base.utils.exp.setOnClick
import com.xy.base.utils.exp.showToast
import java.util.*
import kotlin.math.min

abstract class TimeSelectAssembly(view: BirthAssemblyView, private val selectSuc:()->Unit = {}):
    BaseAssembly<TimeSelectAssembly.BirthAssemblyView>(view) , DialogImplListener,
    OnWheelChangedListener {

    private var defaultYear = Int.MAX_VALUE
    private var defaultMonth = Int.MAX_VALUE
    private var defaultDay = Int.MAX_VALUE
    private var defaultHour = Int.MAX_VALUE
    private var defaultMin = Int.MAX_VALUE

    private val calendar by lazy { Calendar.getInstance() }

    private val birthRule by lazy { this.view?.onCreateBirthRule()?:"%s/%s/%s" }

    private val birthDay by lazy { createBottomDialog(this) }

    private val birthTv by lazy { this.view?.onBirthTextView() }

    protected var yearWheelView :NumberWheelView?=null
    protected var monthWheelView :NumberWheelView?=null
    protected var dayWheelView :NumberWheelView?=null
    protected var hourWheelView :NumberWheelView?=null
    protected var minWheelView :NumberWheelView?=null

    protected var maxMonth = 0
    protected var maxDay = 0

    private var selectYear:Int = Int.MAX_VALUE
    private var selectMonth:Int = Int.MAX_VALUE
    private var selectDay:Int = Int.MAX_VALUE
    private var selectHour = Int.MAX_VALUE
    private var selectMin = Int.MAX_VALUE

    protected fun getCurrentYear() = calendar.get(Calendar.YEAR)
    protected fun getCurrentMonth() = calendar.get(Calendar.MONTH) + 1
    protected fun getCurrentDay() = calendar.get(Calendar.DAY_OF_MONTH)
    protected fun getCurrentHour() = calendar.get(Calendar.HOUR)
    protected fun getCurrentMin() = calendar.get(Calendar.MINUTE)

    protected fun getSelectYear():Int = yearWheelView?.getCurrentItem<Int>()?:-1
    protected fun getSelectMonth():Int = monthWheelView?.getCurrentItem<Int>()?:-1
    protected fun getSelectDay():Int = dayWheelView?.getCurrentItem<Int>()?:-1
    protected fun getSelectHour():Int = hourWheelView?.getCurrentItem<Int>()?:-1
    protected fun getSelectMin():Int = minWheelView?.getCurrentItem<Int>()?:-1

    override fun onCreateInit() {
        super.onCreateInit()
        this.view?.onSelectBirthButton()?.setOnClick{
            birthDay?.show()
        }
    }

    /**
     * 设置初始化的时间
     */
    fun setDefaultValue(defaultYear:Int,defaultMonth:Int,defaultDay:Int,defaultHour:Int,defaultMin:Int){
        this.defaultYear = defaultYear
        this.defaultMonth = defaultMonth
        this.defaultDay = defaultDay
        this.defaultHour = defaultHour
        this.defaultMin = defaultMin

        this.selectYear = defaultYear
        this.selectMonth = defaultMonth
        this.selectDay = defaultDay
        this.selectHour = defaultHour
        this.selectMin = selectMin


        yearWheelView?.setDefaultValue(defaultYear)
        resetMonth(defaultYear)
        monthWheelView?.setDefaultValue(defaultMonth)
        dayWheelView?.setDefaultValue(defaultDay)
        setSelect(defaultDay, defaultMonth, defaultDay,defaultHour,defaultMin)
    }


    override fun dialogLayoutRes(): Int? = this.view?.onCreateBirthDialogLayoutRes()

    override fun dialogProportion(): Double = 1.0

    override fun dialogInitView(dialog: BaseDialog) {
        super.dialogInitView(dialog)
        initView(dialog)
        setListener()
        view?.onCreateDialogCancelView(dialog)?.setOnClick{
            dialog.dismiss()
        }
        view?.onCreateDialogSureView(dialog)?.setOnClick{
            val selectYear = getSelectYear()
            val selectMonth = getSelectMonth()
            val selectDay = getSelectDay()
            val selectHour = getSelectHour()
            val selectMin = getSelectMin()
            setSelect(selectYear, selectMonth, selectDay,selectHour,selectMin)
        }
        initData()
    }

    private fun initView(dialog: BaseDialog){
        yearWheelView = this.view?.onCreateYearWheelView(dialog)
        monthWheelView = this.view?.onCreateMonthWheelView(dialog)
        dayWheelView = this.view?.onCreateDayWheelView(dialog)
        hourWheelView = this.view?.onCreateHourWheelView(dialog)
        minWheelView = this.view?.onCreateMinWheelView(dialog)
    }

    open fun setListener(){
        yearWheelView?.setOnWheelChangedListener(this)
        monthWheelView?.setOnWheelChangedListener(this)
        dayWheelView?.setOnWheelChangedListener(this)
        hourWheelView?.setOnWheelChangedListener(this)
    }

    open fun initData(){
        val currentYear = getCurrentYear()
        yearWheelView?.setRange(currentYear-100,currentYear,1)
        val defaultYear = if (defaultYear != Int.MAX_VALUE) defaultYear else (getCurrentYear() - 18)
        val defaultMonth = if (defaultMonth != Int.MAX_VALUE) defaultMonth else getCurrentMonth()
        val defaultDay = if (defaultDay != Int.MAX_VALUE) defaultDay else getCurrentDay()
        val defaultHour = if (defaultDay != Int.MAX_VALUE) defaultHour else getCurrentHour()
        val defaultMin = if (defaultDay != Int.MAX_VALUE) defaultMin else getCurrentMin()
        yearWheelView?.setDefaultValue(defaultYear)
        resetMonth(defaultYear)
        monthWheelView?.setDefaultValue(defaultMonth)
        resetDay(defaultYear,defaultMonth)
        dayWheelView?.setDefaultValue(defaultDay)
        resetHour(defaultYear,defaultMonth,defaultDay)
        hourWheelView?.setDefaultValue(defaultHour)
        resetMin(defaultYear,defaultMonth,defaultDay,defaultHour)
        minWheelView?.setDefaultValue(defaultMin)
    }


    private fun setSelect(selectYear:Int,selectMonth:Int,selectDay:Int,selectHour:Int,selectMin:Int){
        if (view?.checkSelectMethod(this,selectYear,selectMonth,selectDay,selectHour, selectMin) == true){
            this.selectYear = selectYear
            this.selectMonth = selectMonth
            this.selectDay = selectDay


            val selectYearStr = if (yearWheelView?.isVisible == true) "" else if (selectYear < 10) "0$selectYear" else "$selectYear"
            val selectMonthStr = if (monthWheelView?.isVisible == true) "" else if (selectMonth < 10) "0$selectMonth" else "$selectMonth"
            val selectDayStr = if (dayWheelView?.isVisible == true) "" else if (selectDay < 10) "0$selectDay" else "$selectDay"
            val selectHourStr = if (hourWheelView?.isVisible == true) "" else if (selectHour < 10) "0$selectHour" else "$selectHour"
            val selectMinStr = if (minWheelView?.isVisible == true) "" else if (selectMin < 10) "0$selectMin" else "$selectMin"


            birthTv?.text = String.format(birthRule,selectYearStr,selectMonthStr,selectDayStr,selectHourStr,selectMinStr)
            birthDay?.dismiss()
            selectSuc()
        }
    }

    /**
     * 当年月被选中的时候  修改后面的选中羡慕
     */
    override fun onWheelSelected(view: WheelView?, position: Int) {
        if (view == null)return
        when (view) {
            yearWheelView -> {
                resetMonth(getSelectYear())
            }
            monthWheelView -> {
                resetDay(getSelectYear(),getSelectMonth())
            }
            dayWheelView -> {
                resetHour(getSelectYear(),getSelectMonth(),getSelectDay())
            }
            hourWheelView -> {
                resetMin(getSelectYear(),getSelectMonth(),getSelectDay(),getSelectHour())
            }
        }
    }

    /**
     * 充值month的选择项
     */
    open fun resetMonth(year:Int){}

    /**
     * 充值day的选择项目
     */
    open fun resetDay(year: Int,month:Int){}
    open fun resetHour(year: Int,month:Int,day:Int){}
    open fun resetMin(year: Int,month:Int,day: Int,hour:Int){}

    /**
     * 获取当前年月最大天数
     */
    protected fun getTotalDaysInMonth(year: Int, month: Int): Int {
        return when (month) {
            1, 3, 5, 7, 8, 10, 12 ->31
            4, 6, 9, 11 -> 30
            2 -> {
                // 二月需要判断是否闰年
                if (year <= 0) {
                    return 29
                }
                // 是否闰年：能被4整除但不能被100整除；能被400整除；
                val isLeap = year % 4 == 0 && year % 100 != 0 || year % 400 == 0
                if (isLeap) {
                    29
                } else {
                    28
                }
            }
            else -> 30
        }
    }


    fun isSelectBirth() :Boolean{
        val rule = 0 until Int.MAX_VALUE
        if (selectYear !in rule || selectMonth !in rule || selectDay !in  rule){
            return false
        }
        return  true
    }

    /**
     * 获取当前选中的生日
     */
    fun getSelectBirthDay(method:(Int,Int,Int)->Unit){
        if (!isSelectBirth()){
            getContext()?.showToast(this.view?.unSelectBirthHint())
            return
        }
        method(selectYear,selectMonth,selectDay)
    }


    fun getSelectBirthDayStr(method:(String)->Unit){
        if (!isSelectBirth()){
            getContext()?.showToast(this.view?.unSelectBirthHint())
            return
        }
        method(birthTv?.text?.toString()?:"")
    }


    //    判断是否大于18岁
    fun checkAdult(date: Date?): Boolean {
        val current: Calendar = Calendar.getInstance()
        val birthDay: Calendar = Calendar.getInstance()
        birthDay.time = date
        val year: Int = current.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR)
        if (year > 18) {
            return true
        } else if (year < 18) {
            return false
        }
        // 如果年相等，就比较月份
        val month: Int = current.get(Calendar.MONTH) - birthDay.get(Calendar.MONTH)
        if (month > 0) {
            return true
        } else if (month < 0) {
            return false
        }
        // 如果月也相等，就比较天
        val day: Int = current.get(Calendar.DAY_OF_MONTH) - birthDay.get(Calendar.DAY_OF_MONTH)
        return day >= 0
    }

    override fun onWheelScrolled(view: WheelView?, offset: Int) {}

    override fun onWheelScrollStateChanged(view: WheelView?, state: Int) {}

    override fun onWheelLoopFinished(view: WheelView?) {}



    interface BirthAssemblyView : BaseAssemblyView, DialogCancelSureView {
        fun onSelectBirthButton(): View?
        fun onBirthTextView(): TextView?


        fun onCreateYearWheelView(dialog:BaseDialog):NumberWheelView?
        fun onCreateMonthWheelView(dialog: BaseDialog):NumberWheelView?
        fun onCreateDayWheelView(dialog: BaseDialog):NumberWheelView?
        fun onCreateHourWheelView(dialog: BaseDialog):NumberWheelView?
        fun onCreateMinWheelView(dialog: BaseDialog):NumberWheelView?

        fun onCreateBirthDialogLayoutRes():Int
        fun onCreateBirthRule():String?
        fun unSelectBirthHint():String?
        fun checkSelectMethod(assembly: TimeSelectAssembly, year:Int, month:Int, day:Int, hour:Int, min:Int):Boolean
    }
}