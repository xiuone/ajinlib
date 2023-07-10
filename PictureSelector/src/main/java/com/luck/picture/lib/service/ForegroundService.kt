package com.luck.picture.lib.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.IBinder
import com.luck.picture.lib.config.SelectorProviders.Companion.instance
import com.luck.picture.lib.config.SelectorProviders.selectorConfig
import com.luck.picture.lib.config.SelectMimeType.ofAudio
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.config.SelectorProviders
import androidx.core.app.NotificationCompat
import com.luck.picture.lib.BuildConfig
import com.luck.picture.lib.R
import com.luck.picture.lib.utils.SdkVersionUtils
import java.lang.Exception

/**
 * @author：luck
 * @date：2021/12/2 11:01 上午
 * @describe：ForegroundService
 */
class ForegroundService : Service() {
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        val notification = createForegroundNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        isForegroundServiceIng = true
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        isForegroundServiceIng = false
        stopForeground(true)
        super.onDestroy()
    }

    /**
     * 创建前台通知Notification
     *
     * @return
     */
    private fun createForegroundNotification(): Notification {
        var importance = 0
        if (SdkVersionUtils.isMaxN()) {
            importance = NotificationManager.IMPORTANCE_HIGH
        }
        if (SdkVersionUtils.isO()) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            channel.lightColor = Color.BLUE
            channel.canBypassDnd()
            channel.setBypassDnd(true)
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        val config = instance!!.selectorConfig
        val contentText =
            if (config.chooseMode == ofAudio()) getString(R.string.ps_use_sound) else getString(
                R.string.ps_use_camera
            )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ps_ic_trans_1px)
            .setContentTitle(appName)
            .setContentText(contentText)
            .setOngoing(true)
            .build()
    }

    private val appName: String
        private get() {
            try {
                val packageInfo = packageManager.getPackageInfo(packageName, 0)
                return packageInfo.applicationInfo.loadLabel(packageManager).toString()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ""
        }

    companion object {
        private val CHANNEL_ID =
            BuildConfig.LIBRARY_PACKAGE_NAME + "." + ForegroundService::class.java.name
        private const val CHANNEL_NAME = BuildConfig.LIBRARY_PACKAGE_NAME
        private const val NOTIFICATION_ID = 1
        private var isForegroundServiceIng = false

        /**
         * start foreground service
         *
         * @param context
         */
        fun startForegroundService(context: Context, isCameraForegroundService: Boolean) {
            try {
                if (!isForegroundServiceIng && isCameraForegroundService) {
                    val intent = Intent(context, ForegroundService::class.java)
                    if (SdkVersionUtils.isO()) {
                        context.startForegroundService(intent)
                    } else {
                        context.startService(intent)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * stop foreground service
         *
         * @param context
         */
        fun stopService(context: Context) {
            try {
                if (isForegroundServiceIng) {
                    val foregroundService = Intent(context, ForegroundService::class.java)
                    context.stopService(foregroundService)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}