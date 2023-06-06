package com.jianbian.face.assembly.camera

import android.view.ViewGroup
import com.jianbian.face.view.CameraSurfaceView
import com.xy.base.assembly.base.BaseAssemblyView

interface CameraAssemblyView :BaseAssemblyView ,CameraStatusListener{

    fun onCreateContentView():ViewGroup?
    fun onPreviewFrame(surfaceView:CameraSurfaceView,data: ByteArray?) {}
}