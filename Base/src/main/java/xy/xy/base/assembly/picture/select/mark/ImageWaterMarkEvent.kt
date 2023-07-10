package xy.xy.base.assembly.picture.select.mark

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import picture.luck.picture.lib.config.PictureMimeType
import picture.luck.picture.lib.interfaces.OnBitmapWatermarkEventListener
import picture.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener
import picture.luck.picture.lib.utils.DateUtils
import picture.luck.picture.lib.utils.PictureFileUtils
import xy.xy.base.R

import xy.xy.base.utils.exp.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ImageWaterMarkEvent (private val markRes:Int,
                           private val markWith:Int, private val markHeight:Int,
                           private val right:Int, private val top:Int) :
    OnBitmapWatermarkEventListener {

    private fun createFileImg(context: Context?):String =
        "${context?.getSdImageDir(context?.getResString(R.string.app_name))}${DateUtils.getCreateFileName("Mark_")}.png"

    override fun onAddBitmapWatermark(context: Context?, srcPath: String?, mimeType: String?,
                                      call: OnKeyValueResultCallbackListener?, ) {
        if (PictureMimeType.isHasHttp(srcPath) || PictureMimeType.isHasVideo(mimeType) || context == null) {
            // 网络图片和视频忽略，有需求的可自行扩展
            call?.onCallback(srcPath, "")
        } else {
            // 暂时只以图片为例
            Glide.with(context).asBitmap().sizeMultiplier(0.6f).load(srcPath)
                .into(object : CustomTarget<Bitmap?>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?, ) {
                        val stream = ByteArrayOutputStream()
                        val watermark = context.getBitmapFromRes(markRes,markWith,markHeight)
                        val watermarkBitmap = resource.createWaterMaskRightTop(watermark, right, top)
                        watermarkBitmap?.compress(Bitmap.CompressFormat.JPEG, 60, stream)
                        watermarkBitmap?.recycle()
                        var fos: FileOutputStream? = null
                        var result: String? = null
                        try {
                            val targetFile = File(createFileImg(context))
                            fos = FileOutputStream(targetFile)
                            fos.write(stream.toByteArray())
                            fos.flush()
                            result = targetFile.absolutePath
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } finally {
                            PictureFileUtils.close(fos)
                            PictureFileUtils.close(stream)
                        }
                        call?.onCallback(srcPath, result)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        call?.onCallback(srcPath, "")
                    }
                })
        }
    }
}