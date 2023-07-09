package com.xy.base.listener

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment

interface ContextListener {
    fun getPageContext(): Context?
    fun getCurrentAct(): Activity?
    fun getCurrentFragment():Fragment?=null
}