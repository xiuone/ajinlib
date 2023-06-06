package com.jianbian.face.view

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Point
import android.graphics.Rect
import android.hardware.Camera
import android.view.*
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import com.baidu.idl.face.platform.utils.APIUtils
import com.jianbian.face.FaceManger
import com.jianbian.face.assembly.camera.CameraStatusListener
import com.jianbian.face.utils.CameraUtils
import com.xy.base.utils.exp.getScreenHeight
import com.xy.base.utils.exp.getScreenWidth

class CameraSurfaceView(context: Context?,private var listener:CameraStatusListener) : SurfaceView(context),SurfaceHolder.Callback{
    private val mSurfaceHolder by lazy { holder }
    private var status = false

    var cameraId:Int = 0
    val previewDegree by lazy { displayOrientation() }
    val mDisplayWidth by lazy { getContext()?.getScreenWidth()?:0 }
    val mDisplayHeight by lazy { getContext()?.getScreenHeight()?:0 }

    // 显示Size
    val previewRect by lazy {  Rect() }

    private var mPreviewWidth = 0
    private var mPreviewHight = 0

    private var mSurfaceWidth = 0
    private var mSurfaceHeight = 0


    fun onCreate(){
        mSurfaceHolder.setSizeFromLayout();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        val width = (mDisplayWidth * FaceManger.SURFACE_RATIO).toInt()
        val height = (mDisplayHeight * FaceManger.SURFACE_RATIO).toInt()
        val gravity = Gravity.CENTER_VERTICAL or  Gravity.CENTER_HORIZONTAL
        layoutParams = FrameLayout.LayoutParams(width, height, gravity)
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
        status = false
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, mSurfaceWidth: Int, mSurfaceHeight: Int) {
        this.mSurfaceWidth = mSurfaceWidth
        this.mSurfaceHeight = mSurfaceHeight
        if (holder.surface == null) {
            return
        }
        listener.surfaceChanged(holder,format,mSurfaceWidth,mSurfaceHeight)
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        status = true
    }


    fun setPreview(mCameraParam:Camera.Parameters?){
        mCameraParam?.pictureFormat = PixelFormat.JPEG
        mCameraParam?.set("rotation",previewDegree)

        val point = CameraUtils.getBestPreview(mCameraParam, Point(mDisplayWidth, mDisplayHeight))
        mPreviewWidth = point.x
        mPreviewHight = point.y
        previewRect.set(0, 0, mPreviewHight,mPreviewWidth)
        mCameraParam?.setPreviewSize(mPreviewWidth, mPreviewHight)
    }

    fun removeListener(){
        mSurfaceHolder.removeCallback(this)
    }

    fun resetListener(){
        mSurfaceHolder.removeCallback(this)
        mSurfaceHolder.addCallback(this)
    }



    fun getStatus() = status

    fun getSurfaceHolder(): SurfaceHolder = mSurfaceHolder

    /**
     * 获取旋转角度
     */
    private fun displayOrientation(): Int {
        val windowManager = context?.getSystemService(FragmentActivity.WINDOW_SERVICE);
        if (windowManager is WindowManager){
            var degrees = when (windowManager.defaultDisplay.rotation) {
                Surface.ROTATION_0 -> 0
                Surface.ROTATION_90 -> 90
                Surface.ROTATION_180 -> 180
                Surface.ROTATION_270 -> 270
                else -> 0
            }
            var result = (0 - degrees + 360) % 360
            if (APIUtils.hasGingerbread()) {
                val info = Camera.CameraInfo()
                Camera.getCameraInfo(cameraId, info)
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    result = (info.orientation + degrees) % 360
                    result = (360 - result) % 360
                } else {
                    result = (info.orientation - degrees + 360) % 360
                }
            }
            return result
        }
        return 0
    }
}