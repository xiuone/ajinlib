package compress.zibin.luban.image

import android.content.Context
import android.net.Uri
import compress.zibin.luban.Luban
import compress.zibin.luban.OnCompressListener
import picture.luck.picture.lib.engine.CompressFileEngine
import picture.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener
import java.io.File

class ImageFileCompressEngine : CompressFileEngine {
    override fun onStartCompress(context: Context, source: ArrayList<Uri>, call: OnKeyValueResultCallbackListener) {
        Luban.with(context)
            .load(source)
            .ignoreBy(100)
            .setCompressListener(object : OnCompressListener {
                override fun onSuccess(source: String?, compressFile: File?) {
                    call.onCallback(source, compressFile!!.absolutePath)
                }

                override fun onError(source: String?, e: Throwable?) {
                    call.onCallback(source, null)
                }
            }).launch()
    }
}