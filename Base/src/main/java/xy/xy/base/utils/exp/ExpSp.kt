package xy.xy.base.utils.exp

import android.content.Context
import android.content.SharedPreferences
private var mSP: SharedPreferences? = null
private var mEditor: SharedPreferences.Editor? = null

private fun Context.getSharedPreferences() :SharedPreferences?{
    if (mSP == null) {
        mSP = getSharedPreferences(packageName, Context.MODE_PRIVATE)
        mEditor = mSP?.edit()
    }
    return mSP
}

private fun Context.getEditor():SharedPreferences.Editor?{
    getSharedPreferences()
    return mEditor
}


@Synchronized
fun Context.getSpString(key: String?, defValue: String?): String? =
    getSharedPreferences()?.getString(key, defValue)?:defValue


@Synchronized
fun Context.setSpString(key: String?, value: String?) {
    getEditor()?.putString(key, value)
    getEditor()?.commit()
}


@Synchronized
fun Context.getSpInt(key: String?, defValue: Int): Int =
    getSharedPreferences()?.getInt(key, defValue) ?:defValue


@Synchronized
fun Context.setSpInt(key: String?, value: Int) {
    getEditor()?.putInt(key, value)
    getEditor()?.commit()
}


@Synchronized
fun Context.getSpLong(key: String?, defValue: Long=0): Long =
    getSharedPreferences()?.getLong(key, defValue)?:defValue


@Synchronized
fun Context.setSpLong(key: String?, value: Long) {
    getEditor()?.putLong(key, value)
    getEditor()?.commit()
}

@Synchronized
fun Context.getSpBoolean(key: String?, defValue: Boolean): Boolean =
    getSharedPreferences()?.getBoolean(key, defValue)?:defValue

@Synchronized
fun Context.setSpBoolean(key: String?, value: Boolean) {
    getEditor()?.putBoolean(key, value)
    getEditor()?.commit()
}

@Synchronized
fun Context.getSpFloat(key: String?, defValue: Float): Float =
    getSharedPreferences()?.getFloat(key, defValue)?:defValue

@Synchronized
fun Context.setSpFloat(key: String?, value: Float) {
    getEditor()?.putFloat(key, value)
    getEditor()?.commit()
}