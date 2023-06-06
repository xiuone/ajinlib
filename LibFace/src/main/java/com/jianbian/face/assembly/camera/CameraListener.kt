package com.jianbian.face.assembly.camera

import com.jianbian.face.view.CameraSurfaceView

interface CameraListener {
    fun startPreview(surfaceView: CameraSurfaceView)
    fun stopPreview()
    fun onPreviewFrame(surfaceView: CameraSurfaceView,data: ByteArray?) {}
}