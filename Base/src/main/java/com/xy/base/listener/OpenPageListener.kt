package com.xy.base.listener

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback

interface OpenPageListener {
    fun registerLaunch(tag: String,launcher: ActivityResultCallback<ActivityResult>)
    /**
     * 进入历史界面
     */
    fun startActivityForResult(intent: Intent)

    /**
     * 进入历史界面
     */
    fun startActivityForResult(tag:String,intent: Intent)
}