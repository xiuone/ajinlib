package com.xy.baselib.exp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import com.xy.baselib.receiver.NetType


/**
 * 判断是否有网络连接
 * @param context
 * @return
 */
fun Context.isNetworkConnected(): Boolean {
    val mConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE)
    if (mConnectivityManager !is ConnectivityManager) { // 为空则认为无网络
        return false;
    }
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

/**
 * 获取到运营商的名字
 */
fun Context.getOperatorName() :String{
    val telephonyManager = getSystemService (Context.TELEPHONY_SERVICE) as TelephonyManager
    return telephonyManager.simOperatorName?:""
}

@SuppressLint("MissingPermission")
private fun Context.getNetworkState() :NetType {
    // 若不是WIFI，则去判断是2G、3G、4G网
    val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    when (telephonyManager.networkType) {
        TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_CDMA,
        TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN
        -> return NetType.G2
        TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0,
        TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA,
        TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP
        -> return NetType.G3
        TelephonyManager.NETWORK_TYPE_LTE -> return NetType.G4
        TelephonyManager.NETWORK_TYPE_NR -> return NetType.G5
        else -> return NetType.MOBILE
    }
}

/**
 * 判断MOBILE网络是否可用
 * @param context
 * @return
 */
fun Context.getCurrentNetType(): NetType {
    if (!isNetworkConnected())return NetType.NOTWORK
    if (isWifiConnected())return NetType.WIFI
    val openPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
    return if (openPermission) { NetType.MOBILE }else getCurrentNetType()
}