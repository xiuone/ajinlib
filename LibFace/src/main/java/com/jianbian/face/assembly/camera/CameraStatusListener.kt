package com.jianbian.face.assembly.camera

import android.hardware.Camera
import android.view.SurfaceHolder

interface CameraStatusListener {
    fun onError(p0: Int, p1: Camera?) {}
    fun onPreviewFrame(data: ByteArray?, p1: Camera?) {}
    fun surfaceChanged(holder: SurfaceHolder, format: Int, mSurfaceWidth: Int, mSurfaceHeight: Int) {}
}