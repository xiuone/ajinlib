package com.xy.qr

import com.hjq.permissions.Permission
import com.king.zxing.util.CodeUtils
import com.xy.base.assembly.base.BaseAssembly
import com.xy.base.utils.exp.*
import com.xy.base.utils.permission.PermissionCallBack
import com.xy.base.utils.permission.PermissionRequestMode
import com.xy.base.utils.permission.requestPermission
import com.xy.base.utils.runBackThread
import com.xy.base.utils.runMain

class QrCreateAssembly(view: QrCreateAssemblyView) : BaseAssembly<QrCreateAssemblyView>(view),PermissionCallBack{

    private val historyKey by lazy { this.view?.createSaveHistoryKey() }

    private val saveView by lazy { this.view?.createSaveView() }
    private val imageList by lazy { this.view?.createQrImageViewList()?:ArrayList() }
    private val saveButton by lazy { this.view?.createSaveQrButton() }
    private val changeButton by lazy { this.view?.createChangeQrButton() }
    private val logoBitmap by lazy { this.view?.createLogoBitmap() }
    private val bitmapSize by lazy { this.view?.createBitmapSize()?:600 }

    private val deniedDialog by lazy { this.view?.onCreatePermissionDenied() }
    private val reasonDialog by lazy { this.view?.onCreatePermissionReason() }

    override fun onCreateInit() {
        super.onCreateInit()

        saveButton?.setOnClick{
            getContext()?.requestPermission(reasonDialog,deniedDialog,this, PermissionRequestMode(
                arrayOf(Permission.WRITE_EXTERNAL_STORAGE),getContext()?.getResString(R.string.qr_permission_hint)))
        }
        changeButton?.setOnClick{
            createQr(this.view?.createNewContent())
        }
        var historyQr = getContext()?.getSpString(historyKey,"")
        if (historyQr.isNullOrEmpty())
            historyQr = this.view?.createNewContent()
        createQr(getContext()?.getSpString(historyKey,historyQr))

    }

    /**
     * 创建Qr 创建二维码
     */
    private fun createQr(content:String?){
        runBackThread({
            if (content == null)return@runBackThread
            val bitmap = CodeUtils.createQRCode(content, bitmapSize, logoBitmap)?:return@runBackThread
            runMain({
                for (imageView in imageList){
                    imageView?.setImageBitmap(bitmap)
                }
            })
        })
    }

    /**
     * 请求权限成功回调
     */
    override fun onGranted() {
        getContext()?.run {
            val filePath = getSdImagePath(getAppName(),"${System.currentTimeMillis()}.png")
            val sucHint = String.format(getResString(R.string.qr_permission_save_suc_hint),filePath)
            val errorHint = getResString(R.string.qr_permission_save_error_hint)
            saveView?.saveImageToGallery(filePath,sucHint,errorHint)
        }
    }
}