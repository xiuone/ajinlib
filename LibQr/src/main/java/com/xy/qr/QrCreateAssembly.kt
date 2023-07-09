package com.xy.qr

import com.king.zxing.util.CodeUtils
import com.xy.base.assembly.base.BaseAssemblyWithContext
import com.xy.base.permission.OnPermissionCallback
import com.xy.base.permission.Permission
import com.xy.base.permission.XXPermissions
import com.xy.base.utils.exp.*
import com.xy.base.utils.runBackThread
import com.xy.base.utils.runMain

class QrCreateAssembly(view: QrCreateAssemblyView) : BaseAssemblyWithContext<QrCreateAssemblyView>(view),OnPermissionCallback{

    private val historyKey by lazy { this.view?.createSaveHistoryKey() }

    private val saveView by lazy { this.view?.createSaveView() }
    private val imageList by lazy { this.view?.createQrImageViewList()?:ArrayList() }
    private val saveButton by lazy { this.view?.createSaveQrButton() }
    private val changeButton by lazy { this.view?.createChangeQrButton() }
    private val logoBitmap by lazy { this.view?.createLogoBitmap() }
    private val bitmapSize by lazy { this.view?.createBitmapSize()?:600 }
    private val interceptor by lazy { this.view?.onCreateIPermissionInterceptor() }

    override fun onCreateInit() {
        super.onCreateInit()

        saveButton?.setOnClick{
            val act = getCurrentAct()
            val interceptor = interceptor
            if (act != null && interceptor != null){
                XXPermissions.with(act,interceptor).permission(Permission.WRITE_EXTERNAL_STORAGE).request(this)
            }
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

    override fun onGranted(permissions: List<String?>, allGranted: Boolean) {
        getContext()?.run {
            val filePath = getSdImagePath(getAppName(),"${System.currentTimeMillis()}.png")
            val sucHint = String.format(getResString(R.string.qr_permission_save_suc_hint),filePath)
            val errorHint = getResString(R.string.qr_permission_save_error_hint)
            saveView?.saveImageToGallery(filePath,sucHint,errorHint)
        }
    }
}