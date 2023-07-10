package xy.xy.base.assembly.picture.select.compress

import android.content.Context
import android.net.Uri
import compress.zibin.luban.Luban
import compress.zibin.luban.OnNewCompressListener
import picture.luck.picture.lib.utils.DateUtils
import picture.luck.picture.lib.engine.CompressFileEngine
import picture.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener
import java.io.File

class ImageFileCompressEngine :
    CompressFileEngine {
    override fun onStartCompress(context: Context?, source: ArrayList<Uri>?,
                                 call: OnKeyValueResultCallbackListener?) {
        source?.run {
            Luban.with(context).load(this).ignoreBy(100).setRenameListener { filePath ->
                val indexOf = filePath.lastIndexOf(".")
                val postfix = if (indexOf != -1) filePath.substring(indexOf) else ".jpg"
                DateUtils.getCreateFileName("CMP_").toString() + postfix
            }.setCompressListener(object : OnNewCompressListener {
                override fun onStart() {}
                override fun onSuccess(source: String?, compressFile: File) {
                    call?.onCallback(source, compressFile.absolutePath)
                }

                override fun onError(source: String, e: Throwable) {
                    call?.onCallback(source, null)
                }
            }).launch()
        }
    }
}