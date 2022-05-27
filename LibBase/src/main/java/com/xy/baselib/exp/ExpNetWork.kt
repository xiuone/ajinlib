package com.xy.baselib.exp

import android.content.Context
import android.net.ConnectivityManager
import android.telephony.TelephonyManager


/**
 * 判断是否有网络连接
 * @param context
 * @return
 */
fun Context.isNetworkConnected(): Boolean {
    val mConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val mNetworkInfo = mConnectivityManager.activeNetworkInfo
    return mNetworkInfo != null && mNetworkInfo.isAvailable
}


/**
 * 判断WIFI网络是否可用
 * @param context
 * @return
 */
fun Context.isWifiConnected(): Boolean {
    val mConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val mWiFiNetworkInfo = mConnectivityManager.activeNetworkInfo
    return mWiFiNetworkInfo != null && mWiFiNetworkInfo.type == ConnectivityManager.TYPE_WIFI && mWiFiNetworkInfo.isAvailable
}


/**
 * 判断MOBILE网络是否可用
 * @param context
 * @return
 */
fun Context.isMobileConnected(): Boolean {
    val mConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val mMobileNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
    return mMobileNetworkInfo != null && mMobileNetworkInfo.isAvailable
}