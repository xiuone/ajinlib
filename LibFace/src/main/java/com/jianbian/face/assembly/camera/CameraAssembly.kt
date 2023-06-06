package com.jianbian.face.assembly.camera

import android.hardware.Camera
import android.view.SurfaceHolder
import androidx.lifecycle.LifecycleOwner
import com.jianbian.face.utils.CameraUtils
import com.jianbian.face.view.CameraSurfaceView
import com.xy.base.assembly.base.BaseAssembly

class CameraAssembly(view: CameraAssemblyView,private var cameraListener:CameraListener?) :BaseAssembly<CameraAssemblyView>(view) ,CameraStatusListener{
    private val listener by lazy { CameraImpl(this) }

    private var mCamera :Camera? = null
    private val mCameraParam by lazy { mCamera?.parameters }

    private val surfaceView by lazy { CameraSurfaceView(getContext(),this) }


    override fun onCreateInit() {
        super.onCreateInit()
        this.view?.onCreateContentView()?.addView(surfaceView)
    }

    override fun onResume(owner: LifecycleOwner?) {
        super.onResume(owner)
        startPreview()
    }

    override fun onPause(owner: LifecycleOwner?) {
        super.onPause(owner)
        stopPreview()
    }


    /**
     * 打开相机
     */
    private fun openCamera(): Camera? {
        var camera: Camera?=null
        try {
            val numCameras = Camera.getNumberOfCameras()
            if (numCameras == 0) {
                return null
            }
            var index = 0
            while (index < numCameras) {
                val cameraInfo = Camera.CameraInfo()
                Camera.getCameraInfo(index, cameraInfo)
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    break
                }
                index++
            }
            val number = if (index < numCameras) index else 0
            camera = Camera.open(number)
        }catch (e:Exception){

        }

        return camera
    }

    /**
     * 打开预览
     */
    private fun startPreview() {
        surfaceView.resetListener()
        if (mCamera == null) {
            mCamera = openCamera()?:return
        }
        mCamera?.setDisplayOrientation(surfaceView.previewDegree)
        surfaceView.setPreview(mCameraParam)
        try {
            mCamera?.parameters = mCameraParam
            mCamera?.setPreviewDisplay(surfaceView.getSurfaceHolder())
            mCamera?.stopPreview()
            mCamera?.setErrorCallback(listener)
            mCamera?.setPreviewCallback(listener)
            mCamera?.startPreview()
        } catch (e: RuntimeException) {
            e.printStackTrace()
            CameraUtils.releaseCamera(mCamera)
            mCamera = null
        } catch (e: Exception) {
            e.printStackTrace()
            CameraUtils.releaseCamera(mCamera)
            mCamera = null
        }
        cameraListener?.startPreview(surfaceView)
    }

    /**
     * 停止预览
     */
    private fun stopPreview() {
        try {
            mCamera?.setErrorCallback(null)
            mCamera?.setPreviewCallback(null)
            mCamera?.stopPreview()
        } catch (e: RuntimeException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            CameraUtils.releaseCamera(mCamera)
        }
        mCamera = null
        surfaceView.removeListener()
        cameraListener?.stopPreview()
    }

    override fun onPreviewFrame(data: ByteArray?, p1: Camera?) {
        super.onPreviewFrame(data, p1)
        cameraListener?.onPreviewFrame(surfaceView,data)
    }

    /**
     * 视图大小改变
     */
    override fun surfaceChanged(holder: SurfaceHolder, format: Int, mSurfaceWidth: Int, mSurfaceHeight: Int) = startPreview()


    override fun onDestroyed(owner: LifecycleOwner) {
        super.onDestroyed(owner)
        cameraListener = null
    }
}