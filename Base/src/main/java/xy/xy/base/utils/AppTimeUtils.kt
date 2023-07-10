package xy.xy.base.utils

import java.text.SimpleDateFormat
import java.util.*

object AppTimeUtils {
    /**
     * 获取多少时间之前
     */
    open fun getTimeShowString(milliseconds: Long): String? {
        var dataString: String? = null
        val timeStringBy24: String
        val currentTime = Date(milliseconds)
        val today = Date()
        val todayStart = Calendar.getInstance()
        todayStart[Calendar.HOUR_OF_DAY] = 0
        todayStart[Calendar.MINUTE] = 0
        todayStart[Calendar.SECOND] = 0
        todayStart[Calendar.MILLISECOND] = 0
        val todaybegin = todayStart.time
        val yesterdaybegin = Date(todaybegin.time - 3600 * 24 * 1000)
        val preyesterday = Date(yesterdaybegin.time - 3600 * 24 * 1000)
        dataString = if (!currentTime.before(todaybegin)) {
            "今天"
        } else if (!currentTime.before(yesterdaybegin)) {
            "昨天"
        } else if (!currentTime.before(preyesterday)) {
            "前天"
        } else if (isSameWeekDates(currentTime, today)) {
            getWeekOfDate(currentTime)
        } else if (isSameMonthDates(currentTime, today) || isToadyYear(currentTime)) {
            val dateformatter = SimpleDateFormat("MM-dd", Locale.getDefault())
            dateformatter.format(currentTime)
        } else {
            val dateformatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateformatter.format(currentTime)
        }
        val timeformatter24 = SimpleDateFormat("HH:mm", Locale.getDefault())
        timeStringBy24 = timeformatter24.format(currentTime)
        return "$dataString $timeStringBy24"
    }


    /**
     * 判断两个日期是否在同一周
     *
     * @param date1
     * @param date2
     * @return
     */
    private fun isSameWeekDates(date1: Date?, date2: Date?): Boolean {
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.time = date1
        cal2.time = date2
        val subYear = cal1[Calendar.YEAR] - cal2[Calendar.YEAR]
        if (0 == subYear) {
            if (cal1[Calendar.WEEK_OF_YEAR] == cal2[Calendar.WEEK_OF_YEAR]) return true
        } else if (1 == subYear && 11 == cal2[Calendar.MONTH]) {
            // 如果12月的最后一周横跨来年第一周的话则最后一周即算做来年的第一周
            if (cal1[Calendar.WEEK_OF_YEAR] == cal2[Calendar.WEEK_OF_YEAR]) return true
        } else if (-1 == subYear && 11 == cal1[Calendar.MONTH]) {
            if (cal1[Calendar.WEEK_OF_YEAR] == cal2[Calendar.WEEK_OF_YEAR]) return true
        }
        return false
    }

    private fun isSameMonthDates(date1: Date?, date2: Date?): Boolean {
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.time = date1
        cal2.time = date2
        return cal1[Calendar.YEAR] == cal2[Calendar.YEAR] && cal1[Calendar.MONTH] == cal2[Calendar.MONTH]
    }

    //    判断是否是今年
    private fun isToadyYear(date1: Date?): Boolean {
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.time = date1
        return cal1[Calendar.YEAR] == cal2[Calendar.YEAR]
    }


    private fun getWeekOfDate(date: Date?): String? {
        val weekDaysName = arrayOf("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六")
        // String[] weekDaysCode = { "0", "1", "2", "3", "4", "5", "6" };
        val calendar = Calendar.getInstance()
        calendar.time = date
        val intWeek = calendar[Calendar.DAY_OF_WEEK] - 1
        return weekDaysName[intWeek]
    }


    fun formatDateTime(mss: Long): String? {

        var dateTimes: String? = null

        var days = mss / (60 * 60 * 24)

        var hours = (mss % (60 * 60 * 24)) / (60 * 60)

        var minutes = (mss % (60 * 60)) / 60
        var seconds = mss % 60
        when {
            days > 0 -> {
                dateTimes = days.toString() + "天" + hours + "小时" + minutes + "分钟" + seconds + "秒"
            }
            hours > 0 -> {
                dateTimes = hours.toString() + "小时" + minutes + "分钟" + seconds + "秒"
            }
            minutes > 0 -> {
                dateTimes = minutes.toString() + "分钟" + seconds + "秒"
            }
            else -> {
                dateTimes = seconds.toString() + "秒"
            }
        }

        return dateTimes

    }


    /*
* 毫秒转化
*/
    fun formatTime(ms: Long): String? {
        val ss = 1000
        val mi = ss * 60
        val hh = mi * 60
        val dd = hh * 24
        val day = ms / dd
        val hour = (ms - day * dd) / hh
        val minute = (ms - day * dd - hour * hh) / mi
        val second = (ms - day * dd - hour * hh - minute * mi) / ss
        val milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss
        val strDay = if (day < 10) "0$day" else "" + day //天
        val strHour = if (hour < 10) "0$hour" else "" + hour //小时
        val strMinute = if (minute < 10) "0$minute" else "" + minute //分钟
        val strSecond = if (second < 10) "0$second" else "" + second //秒
        var strMilliSecond = if (milliSecond < 10) "0$milliSecond" else "" + milliSecond //毫秒
        strMilliSecond = if (milliSecond < 100) "0$strMilliSecond" else "" + strMilliSecond
        return "$strMinute 分钟 $strSecond 秒"
    }

    /**
     * 毫秒转分
     * @param ms
     * @return
     */
    fun msToM(ms: Int): String {
        var seconds = ms / 1000
        val minutes = seconds / 60
        seconds %= 60

        var m: String = ""
        var s: String = ""

        if (minutes == 0 && seconds == 0)
            seconds = 1

        m = if (minutes < 10)
            "0$minutes"
        else
            "" + minutes

        if (seconds < 10)
            s = "0$seconds"
        else
            s = "" + seconds

        return "$m:$s"
    }


    /**
     * 获取指定某一天的结束时间戳
     *
     * @param timeStamp 毫秒级时间戳
     * @param timeZone  如 GMT+8:00
     * @return
     */
    fun getDailyEndTime(timeStamp: Long?, timeZone: String?): Long {
        val calendar = Calendar.getInstance()
//        calendar.timeZone = TimeZone.getTimeZone(timeZone)
        calendar.timeInMillis = timeStamp!!
        calendar[Calendar.HOUR_OF_DAY] = 23
        calendar[Calendar.MINUTE] = 59
        calendar[Calendar.SECOND] = 59
        calendar[Calendar.MILLISECOND] = 999
        return calendar.timeInMillis
    }


    /*
     * 根据毫秒获得天数
     */
    fun getCurrentDays(timeDistance: Long): Long {

        return (timeDistance / (24 * 60 * 60 * 1000))
    }

     fun getCurrentHour(timeDistance:Long):Long{
        return (timeDistance%(24*60*60*1000))/(60*60*1000)
    }

     fun getCurrentMinute(timeDistance:Long):Long{

        return  (timeDistance%(24*60*60*1000))%(60*60*1000)/(60*1000)
     }

      fun getCurrentMills(timeDistance:Long):Long{

        return (timeDistance%(24*60*60*1000))%(60*60*1000)%(60*1000)/1000
      }

    // 获得当天近7天时间
    fun getWeekFromNow(): Date {
        val cal = Calendar.getInstance()
        cal.timeInMillis = getTimesmorning() - 3600 * 24 * 1000 * 7
        return cal.time
    }

    // 获得当天0点时间
    fun getTimesmorning(): Long {
        val cal = Calendar.getInstance()
        cal[Calendar.HOUR_OF_DAY] = 0
        cal[Calendar.SECOND] = 0
        cal[Calendar.MINUTE] = 0
        cal[Calendar.MILLISECOND] = 0
        return cal.timeInMillis
    }

    // 获得本周日24点时间
    fun getTimesWeeknight(): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = getTimesWeekmorning()
        cal.add(Calendar.DAY_OF_WEEK, 7)
        return cal.timeInMillis
    }

    // 获得本周一0点时间
    fun getTimesWeekmorning(): Long {

        val cal = Calendar.getInstance()
        cal[cal[Calendar.YEAR], cal[Calendar.MONDAY], cal[Calendar.DAY_OF_MONTH], 0, 0] = 0
        cal[Calendar.DAY_OF_WEEK] = Calendar.MONDAY
        return cal.timeInMillis
    }
//   当天是否第一次进入app，如果是的，并且开关打开，就需要进行提醒
//   记录上次启动app时间，只要小于第二天的开始时间就需要提醒


}