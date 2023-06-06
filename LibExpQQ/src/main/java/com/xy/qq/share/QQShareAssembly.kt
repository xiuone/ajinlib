package com.xy.qq.share

import android.graphics.Bitmap
import android.os.Bundle
import androidx.lifecycle.LifecycleOwner
import com.tencent.connect.share.QQShare
import com.tencent.connect.share.QzoneShare
import com.tencent.tauth.IUiListener
import com.tencent.tauth.UiError
import com.xy.base.assembly.base.BaseAssembly
import com.xy.base.assembly.base.BaseAssemblyView
import com.xy.base.utils.Logger
import com.xy.base.utils.exp.deleteFile
import com.xy.base.utils.exp.getAppName
import com.xy.base.utils.exp.showToast
import com.xy.qq.QQManger
import java.io.File
import java.io.FileOutputStream
import kotlin.collections.ArrayList

class QQShareAssembly(view: QQShareAssemblyView) :BaseAssembly<QQShareAssembly.QQShareAssemblyView>(view),IUiListener{
    private var temporaryPath:String? = null
    private val qqShareFileDirName by lazy { "qqShare" }

    fun share(bean:QQShareContent) {
        val activity = this.view?.getCurrentAct()?:return
        temporaryPath?.deleteFile()
        val params = Bundle()
        val file = File("${activity.filesDir}/$qqShareFileDirName")
        if (file.exists() && file.isDirectory){
            val fileList = file.listFiles()
            for (childFile in fileList){
                childFile.delete()
            }
        }
        temporaryPath = null
        if (bean.bitmap != null) {
            temporaryPath = "${activity.filesDir}/$qqShareFileDirName/${System.currentTimeMillis()}.png"
            val file = File(temporaryPath)
            if (file.exists()) {
                file.delete()
            }
            val out = FileOutputStream(file)
            bean.bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        if (bean.type == QQShareSceneEnum.Friend) {
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT)
            params.putString(QQShare.SHARE_TO_QQ_TITLE, bean.url ?: activity?.getAppName())
            if (bean.content != null)params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, bean.content)
            if (bean.url != null)params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, bean.url)
            if (temporaryPath != null) params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, temporaryPath)
            params.putString(QQShare.SHARE_TO_QQ_APP_NAME, activity?.getAppName())
            QQManger.mTencent.shareToQQ(activity,params,this)
        } else {
            params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_NO_TYPE)
            params.putString(QzoneShare.SHARE_TO_QQ_APP_NAME, activity?.getAppName())
            params.putString(QQShare.SHARE_TO_QQ_TITLE, bean.title ?: activity?.getAppName())
            if (bean.content != null)params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, bean.content)
            if (bean.url != null)params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, bean.url)
            val imageList = bean.imgUrls?: ArrayList()
            temporaryPath?.run { imageList.add(this) }
            params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageList)
            QQManger.mTencent.shareToQzone(activity, params, this)
        }
    }



    override fun onComplete(o: Any) {
        temporaryPath?.deleteFile()
        Logger.d("QQShareUtils   onComplete:")
        getContext()?.showToast(this.view?.onComplete())
    }

    override fun onError(uiError: UiError) {
        temporaryPath?.deleteFile()
        Logger.d("QQShareUtils   onError:")
        getContext()?.showToast(this.view?.onError())
    }

    override fun onCancel() {
        temporaryPath?.deleteFile()
        Logger.d("QQShareUtils   onCancel:")
        getContext()?.showToast(this.view?.onCancel())
    }

    override fun onWarning(p0: Int) {
        Logger.d("QQShareUtils   onWarning:$p0")
    }


    interface QQShareAssemblyView:BaseAssemblyView{
        fun onComplete():String?
        fun onError():String?
        fun onCancel():String?
    }

    override fun onDestroyed(owner: LifecycleOwner) {
        super.onDestroyed(owner)
        temporaryPath?.deleteFile()
    }
}