package com.jianbian.baselib.utils

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import androidx.annotation.RequiresPermission

object NetworkUtils {
    private val TAG = "NetworkUtils"

    private fun NetworkUtils() {
        throw UnsupportedOperationException(this.javaClass.simpleName + " cannot be instantiated")
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var activeNetworkInfo: NetworkInfo? = null
        if (connectivityManager != null) {
            activeNetworkInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = connectivityManager.activeNetwork
                connectivityManager.getNetworkInfo(network)
            } else {
                connectivityManager.activeNetworkInfo
            }
        }
        return activeNetworkInfo != null && activeNetworkInfo.isAvailable
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var activeNetworkInfo: NetworkInfo? = null
        if (connectivityManager != null) {
            activeNetworkInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = connectivityManager.activeNetwork
                connectivityManager.getNetworkInfo(network)
            } else {
                connectivityManager.activeNetworkInfo
            }
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }


    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    private fun isNetworkAvailable(
        context: Context,
        type: Int
    ): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                ?: return false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (network in connectivityManager.allNetworks) {
                val networkInfo = connectivityManager.getNetworkInfo(network)
                if (networkInfo != null && networkInfo.type == type && networkInfo.isAvailable) {
                    return true
                }
            }
        } else {
            for (networkInfo in connectivityManager.allNetworkInfo) {
                if (networkInfo != null && networkInfo.type == type && networkInfo.isAvailable) {
                    return true
                }
            }
        }
        return false
    }


    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    public fun isNetworkConnected(context: Context, type: Int): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                ?: return false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (network in connectivityManager.allNetworks) {
                val networkInfo = connectivityManager.getNetworkInfo(network)
                if (networkInfo != null && networkInfo.type == type && networkInfo.isConnected) {
                    return true
                }
            }
        } else {
            for (networkInfo in connectivityManager.allNetworkInfo) {
                if (networkInfo != null && networkInfo.type == type && networkInfo.isConnected) {
                    return true
                }
            }
        }
        return false
    }


    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isWifiAvailable(context: Context): Boolean {
        return isNetworkAvailable(context, ConnectivityManager.TYPE_WIFI)
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isWifiConnected(context: Context): Boolean {
        return isNetworkConnected(context, ConnectivityManager.TYPE_WIFI)
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isMobileAvailable(context: Context): Boolean {
        return isNetworkAvailable(context, ConnectivityManager.TYPE_MOBILE)
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isMobileConnected(context: Context): Boolean {
        return isNetworkConnected(context, ConnectivityManager.TYPE_MOBILE)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isVpnAvailable(context: Context): Boolean {
        return isNetworkAvailable(context, ConnectivityManager.TYPE_VPN)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isVpnConnected(context: Context): Boolean {
        return isNetworkConnected(context, ConnectivityManager.TYPE_VPN)
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isBluetoothAvailable(context: Context): Boolean {
        return isNetworkAvailable(context, ConnectivityManager.TYPE_BLUETOOTH)
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isBluetoothConnected(context: Context): Boolean {
        return isNetworkConnected(context, ConnectivityManager.TYPE_BLUETOOTH)
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun getNetworkType(context: Context): Int {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                ?: return -1
        val activeNetworkInfo: NetworkInfo?
        activeNetworkInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            connectivityManager.getNetworkInfo(network)
        } else {
            connectivityManager.activeNetworkInfo
        }
        return activeNetworkInfo?.type ?: -1
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun getNetworkTypeName(context: Context): String? {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                ?: return "UNKNOWN"
        val activeNetworkInfo: NetworkInfo?
        activeNetworkInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            connectivityManager.getNetworkInfo(network)
        } else {
            connectivityManager.activeNetworkInfo
        }
        return if (activeNetworkInfo != null) {
            activeNetworkInfo.typeName
        } else "UNKNOWN"
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun getNetworkSubtype(context: Context): Int {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                ?: return -1
        val activeNetworkInfo: NetworkInfo?
        activeNetworkInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            connectivityManager.getNetworkInfo(network)
        } else {
            connectivityManager.activeNetworkInfo
        }
        return activeNetworkInfo?.subtype ?: -1
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun getNetworkSubtypeName(context: Context): String? {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                ?: return "UNKNOWN"
        val activeNetworkInfo: NetworkInfo?
        activeNetworkInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            connectivityManager.getNetworkInfo(network)
        } else {
            connectivityManager.activeNetworkInfo
        }
        return if (activeNetworkInfo != null) {
            activeNetworkInfo.subtypeName
        } else "UNKNOWN"
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun getNetworkState(context: Context): NetworkInfo.State? {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                ?: return NetworkInfo.State.UNKNOWN
        val activeNetworkInfo: NetworkInfo?
        activeNetworkInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            connectivityManager.getNetworkInfo(network)
        } else {
            connectivityManager.activeNetworkInfo
        }
        return if (activeNetworkInfo != null) {
            activeNetworkInfo.state
        } else NetworkInfo.State.UNKNOWN
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun getNetworkExtraInfo(context: Context): String? {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                ?: return "UNKNOWN"
        val activeNetworkInfo: NetworkInfo?
        activeNetworkInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            connectivityManager.getNetworkInfo(network)
        } else {
            connectivityManager.activeNetworkInfo
        }
        return if (activeNetworkInfo != null) {
            activeNetworkInfo.extraInfo
        } else "UNKNOWN"
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun getNetworkInfo(context: Context): NetworkInfo? {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                ?: return null
        var activeNetworkInfo: NetworkInfo? = null
        activeNetworkInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            connectivityManager.getNetworkInfo(network)
        } else {
            connectivityManager.activeNetworkInfo
        }
        return activeNetworkInfo
    }
}