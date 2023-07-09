package com.xy.qr

import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.xy.base.assembly.base.BaseAssemblyWithContext
import com.xy.base.assembly.base.BaseAssemblyViewWithContext
import com.xy.base.utils.exp.isFileExist
import java.util.ArrayList

class QrScanAssembly(view: QrScanAssemblyView) :
    BaseAssemblyWithContext<QrScanAssembly.QrScanAssemblyView>(view),OnResultCallbackListener<LocalMedia>{





    override fun onCancel() {}


    override fun onResult(result: ArrayList<LocalMedia>?) {
        if (result.isNullOrEmpty()) return
        val localMedia = result[0]
        val path = localMedia.availablePath
        if (path.isFileExist()){

        }
    }


    interface QrScanAssemblyView:BaseAssemblyViewWithContext
}