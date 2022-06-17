package com.xy.baselib.sp

import android.content.Context
import android.content.SharedPreferences
class SPHelperUtils {
    private var mSP: SharedPreferences? = null
    private var mEditor: SharedPreferences.Editor? = null
    private fun getSharedPreferences(context: Context) :SharedPreferences?{
        if (mSP == null) {
            mSP = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
            mEditor = mSP?.edit()
        }
        return mSP
    }

    private fun getEditor(context: Context):SharedPreferences.Editor?{
        getSharedPreferences(context)
        return mEditor
    }


    @Synchronized
    fun getString(context: Context,key: String?, defValue: String?): String? {
        return getSharedPreferences(context)?.getString(key, defValue)
    }

    @Synchronized
    fun setString(context: Context,key: String?, value: String?) {
        getEditor(context)?.putString(key, value)
        getEditor(context)?.commit()
    }

    @Synchronized
    fun getInt(context: Context,key: String?, defValue: Int): Int {
        return getSharedPreferences(context)?.getInt(key, defValue) ?:defValue
    }

    @Synchronized
    fun setInt(context: Context,key: String?, value: Int) {
        getEditor(context)?.putInt(key, value)
        getEditor(context)?.commit()
    }

    @Synchronized
    fun getLong(context: Context,key: String?, defValue: Long): Long {
        return getSharedPreferences(context)?.getLong(key, defValue)?:defValue
    }

    @Synchronized
    fun setLong(context: Context,key: String?, value: Long) {
        getEditor(context)?.putLong(key, value)
        getEditor(context)?.commit()
    }

    @Synchronized
    fun getBoolean(context: Context,key: String?, defValue: Boolean): Boolean {
        return getSharedPreferences(context)?.getBoolean(key, defValue)?:defValue
    }

    @Synchronized
    fun setBoolean(context: Context,key: String?, value: Boolean) {
        getEditor(context)?.putBoolean(key, value)
        getEditor(context)?.commit()
    }

    @Synchronized
    fun getFloat(context: Context,key: String?, defValue: Float): Float {
        return getSharedPreferences(context)?.getFloat(key, defValue)?:defValue
    }

    @Synchronized
    fun setFloat(context: Context,key: String?, value: Float) {
        getEditor(context)?.putFloat(key, value)
        getEditor(context)?.commit()
    }
}