package xy.xy.base.utils.exp

import android.view.View

fun Int.getUnReadNumber() = if(this >99) "99+" else "$this"

fun Int.setUnReadVisibility(view: View?){
    if (this <=0)
        view?.visibility = View.INVISIBLE
    else
        view?.visibility = View.VISIBLE
}

fun Long.getNumber():String{
    return when {
        this >= 10000 -> {
            "${(this / 10000f).keepTwoDecimals()}W"
        }
        this >= 1000 -> {
            "${(this / 1000f).keepTwoDecimals()}K"
        }
        else -> {
            "$this"
        }
    }
}


//   获取0到N的随机数(不包括N)
fun Int.getRandomNum():Int = 0.getRandomNum(this)
fun Int.getRandomNum(max:Int):Int = (this..max).random()
