package com.jianbian.face.assembly.camera

import android.hardware.Camera

class CameraImpl(private val listener:CameraStatusListener?)
    : Camera.PreviewCallback, Camera.ErrorCallback {
    override fun onError(p0: Int, p1: Camera?) {
        listener?.onError(p0,p1)
    }

    override fun onPreviewFrame(p0: ByteArray?, p1: Camera?) {
        listener?.onPreviewFrame(p0,p1)
    }

}