package com.xy.baselib.sp

import android.content.Context
import android.content.SharedPreferences

abstract class SPHelperUtils {
    private var mSP: SharedPreferences? = null
    var mEditor: SharedPreferences.Editor? = null
    fun open(context: Context) {
        if (mSP == null) {
            mSP = context.getSharedPreferences(sharedPreferencesName(), Context.MODE_PRIVATE)
            mEditor = mSP?.edit()
        }
    }

    abstract fun sharedPreferencesName(): String?

    @Synchronized
    fun getString(context: Context,key: String?, defValue: String?): String? {
        open(context)
        return mSP?.getString(key, defValue)
    }

    @Synchronized
    fun setString(context: Context,key: String?, value: String?) {
        open(context)
        mEditor?.putString(key, value)
        mEditor?.commit()
    }

    @Synchronized
    fun getInt(context: Context,key: String?, defValue: Int): Int {
        open(context)
        return mSP?.getInt(key, defValue) ?:defValue
    }

    @Synchronized
    fun setInt(context: Context,key: String?, value: Int) {
        open(context)
        mEditor?.putInt(key, value)
        mEditor?.commit()
    }

    @Synchronized
    fun getLong(context: Context,key: String?, defValue: Long): Long {
        open(context)
        return mSP?.getLong(key, defValue)?:defValue
    }

    @Synchronized
    fun setLong(context: Context,key: String?, value: Long) {
        open(context)
        mEditor?.putLong(key, value)
        mEditor?.commit()
    }

    @Synchronized
    fun getBoolean(context: Context,key: String?, defValue: Boolean): Boolean {
        open(context)
        return mSP?.getBoolean(key, defValue)?:defValue
    }

    @Synchronized
    fun setBoolean(context: Context,key: String?, value: Boolean) {
        open(context)
        mEditor?.putBoolean(key, value)
        mEditor?.commit()
    }

    @Synchronized
    fun getFloat(context: Context,key: String?, defValue: Float): Float {
        open(context)
        return mSP?.getFloat(key, defValue)?:defValue
    }

    @Synchronized
    fun setFloat(context: Context,key: String?, value: Float) {
        open(context)
        mEditor?.putFloat(key, value)
        mEditor?.commit()
    }
}