package com.jianbian.face.assembly.live

import android.graphics.Bitmap
import android.graphics.Rect
import android.media.AudioManager
import androidx.lifecycle.LifecycleOwner
import com.baidu.idl.face.platform.*
import com.baidu.idl.face.platform.model.FaceExtInfo
import com.baidu.idl.face.platform.model.ImageInfo
import com.baidu.idl.face.platform.stat.Ast
import com.jianbian.face.FaceManger
import com.jianbian.face.R
import com.jianbian.face.assembly.camera.CameraListener
import com.jianbian.face.utils.base64ToBitmap
import com.jianbian.face.view.CameraSurfaceView
import com.xy.base.assembly.base.BaseAssembly
import com.xy.base.dialog.base.BaseDialog
import com.xy.base.dialog.listener.DialogImplListener
import com.xy.base.utils.exp.*
import com.xy.base.utils.runBackThread
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class LiveAssembly(view: LiveAssemblyView) :BaseAssembly<LiveAssemblyView>(view) , ILivenessStrategyCallback,DialogImplListener ,
    LiveViewListener,CameraListener {

    private val mDisplayWidth by lazy { getContext()?.getScreenWidth()?:0 }

    private val roundView  by lazy { this.view?.onCreateFaceDetectRoundView() }
    private val soundView by lazy { this.view?.onCreateSoundView() }
    private val animView by lazy { this.view?.onCreateAnimView() }

    private val mFaceConfig by lazy { FaceSDKManager.getInstance().faceConfig }
    private var mILivenessStrategy: ILivenessStrategy? = null
    private var mLivenessType: LivenessTypeEnum? = null
    private val savePaths by lazy { ArrayList<String>() }
    //是否完成
    private var mIsCompletion = false

    private val timeOutDialog by lazy { createCenterDialog(this) }

    override fun onCreateInit() {
        super.onCreateInit()
        soundView?.startInitStatus()
        roundView?.setIsActiveLive(true);
        FaceManger.preInit()
    }


    override fun onResume(owner: LifecycleOwner?) {
        super.onResume(owner)
        this.view?.getCurrentAct()?.volumeControlStream = AudioManager.STREAM_MUSIC
        soundView?.onResume()
        roundView?.setTipTopText(getContext()?.getResString(R.string.detect_face_in))
    }

    override fun onPause(owner: LifecycleOwner?) {
        super.onPause(owner)
        mILivenessStrategy?.reset()
        soundView?.onStop()
        roundView?.setProcessCount(0, mFaceConfig.livenessTypeList.size)
        mIsCompletion = false
    }

    override fun onStop(owner: LifecycleOwner?) {
        super.onStop(owner)
        soundView?.onStop()
    }

    override fun onPreviewFrame(surfaceView: CameraSurfaceView, data: ByteArray?) {
        super.onPreviewFrame(surfaceView, data)
        if (mIsCompletion) {
            return
        }
        if (mILivenessStrategy == null) {
            mILivenessStrategy = FaceSDKManager.getInstance().getLivenessStrategyModule(this)
            mILivenessStrategy?.setPreviewDegree(surfaceView.previewDegree)
            mILivenessStrategy?.setLivenessStrategySoundEnable(soundView?.getEnableSound() == true)
            val detectRect: Rect = FaceManger.getPreviewDetectRect(mDisplayWidth, surfaceView.previewRect.width(), surfaceView.previewRect.height())
            mILivenessStrategy?.setLivenessStrategyConfig(mFaceConfig.livenessTypeList, surfaceView.previewRect, detectRect, this)
        }
        mILivenessStrategy?.livenessStrategy(data)
    }


    override fun startPreview(surfaceView: CameraSurfaceView) {
        mILivenessStrategy?.setPreviewDegree(surfaceView.previewDegree)
    }

    override fun stopPreview() {
        mILivenessStrategy = null
    }


    override fun onLivenessCompletion(
        status: FaceStatusNewEnum?,
        message: String?,
        base64ImageCropMap: HashMap<String, ImageInfo>?,
        base64ImageSrcMap: HashMap<String, ImageInfo>?,
        currentLivenessCount: Int, ) {

        if (mIsCompletion) {
            return
        }
        if (status == FaceStatusNewEnum.OK) {
            mIsCompletion = true
            runBackThread({
                val paths = getBaseImages(base64ImageCropMap)
                savePaths.clear()
                savePaths.addAll(paths)
                this.view?.onGetLiveList(paths)
            })
            return
        }


        if (!(status == FaceStatusNewEnum.OK && mIsCompletion) && status == FaceStatusNewEnum.DetectRemindCodeTimeout) {
            timeOutDialog?.show()
        }

        roundView?.refreshView(status, message, currentLivenessCount)
        animView?.refreshView(status,mLivenessType)

        Ast.getInstance().faceHit("liveness")
    }

    private fun getBaseImages(imageCropMap: HashMap<String, ImageInfo>?): ArrayList<String> {
        val data = ArrayList<String>()
        val list = collectionsList(imageCropMap)
        var index = 0
        while (index < list.size && data.size < 4) {
            val bmpStr = list[index].value.base64
            val bmp: Bitmap = bmpStr.base64ToBitmap()
            val path = "${getContext()?.filesDir}${File.separator}${System.currentTimeMillis()}.png"
            bmp.saveBitmap(path)
            val file = File(path)
            if (file.exists())
                data.add(file.toString())
            index++
        }
        return data
    }

    /**
     * 优质排序
     * @param imageCropMap
     * @return
     */
    private fun collectionsList(imageCropMap: HashMap<String, ImageInfo>?): List<Map.Entry<String, ImageInfo>> {
        if (imageCropMap != null && imageCropMap.size > 0) {
            val list: List<Map.Entry<String, ImageInfo>> = ArrayList<Map.Entry<String, ImageInfo>>(imageCropMap.entries)
            Collections.sort(list) { (key), (key3) ->
                val key1 = key.split("_").toTypedArray()
                val score1 = key1[2]
                val key2 = key3.split("_").toTypedArray()
                val score2 = key2[2]
                score2.toFloat().compareTo(score1.toFloat())
            }
            return list
        }
        return ArrayList()
    }

    /**
     * 设置当前状态
     */
    override fun setCurrentLiveType(liveType: LivenessTypeEnum?) {
        mLivenessType = liveType
    }

    /**
     * 重新设置view
     */
    override fun viewReset() {
        roundView?.setProcessCount(0, 1)
        animStop()
    }


    override fun animStop() {
        animView?.stopAnim()
    }

    override fun dialogLayoutRes(): Int? = this.view?.dialogLayoutRes()

    override fun dialogInitView(dialog: BaseDialog) {
        super.dialogInitView(dialog)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        this.view?.onCreateDialogSureView(dialog)?.setOnClick{
            this.view?.onRecollect()
            dialog.dismiss()
        }
        this.view?.onCreateDialogCancelView(dialog)?.setOnClick{
            this.view?.onReturn()
            dialog.dismiss()
        }
    }


    override fun setFaceInfo(p0: FaceExtInfo?) {}


    override fun onDestroyed(owner: LifecycleOwner) {
        super.onDestroyed(owner)
        for (path in savePaths){
            path.deleteFile()
        }
    }
}