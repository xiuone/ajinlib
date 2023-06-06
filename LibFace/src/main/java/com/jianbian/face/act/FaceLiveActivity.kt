/**
 * Copyright (C) 2017 Baidu Inc. All rights reserved.
 */
package com.jianbian.face.act

import android.os.Bundle
import android.view.*
import com.jianbian.face.assembly.camera.CameraAssembly
import com.jianbian.face.assembly.camera.CameraAssemblyView
import com.jianbian.face.assembly.live.LiveAssembly
import com.jianbian.face.assembly.live.LiveAssemblyView
import com.xy.base.act.ActivityBaseSwipeBack
import com.xy.base.utils.exp.getScreenBrightness
import com.xy.base.utils.exp.setBrightness

/**
 * 活体检测接口
 */
abstract class FaceLiveActivity : ActivityBaseSwipeBack(), CameraAssemblyView,LiveAssemblyView {
    // View
    private val cameraController by lazy { CameraAssembly(this,liveAssembly) }
    private val liveAssembly by lazy { LiveAssembly(this) }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        addAssembly(liveAssembly)
        addAssembly(cameraController)
        setBrightness( getScreenBrightness() + 100)
        onBackPressedHome = true
        setBack(backButton)
    }

    override fun onRecollect() {
        liveAssembly.onResume(this)
        cameraController.onResume(this)
    }

    override fun onReturn() {
        finish()
    }

}