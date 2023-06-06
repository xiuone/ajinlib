package com.xy.base.listener

import android.app.Activity
import android.content.Context

interface ContextListener {
    fun getPageContext(): Context?
    fun getCurrentAct(): Activity?
}