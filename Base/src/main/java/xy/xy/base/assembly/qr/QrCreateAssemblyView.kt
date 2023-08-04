package xy.xy.base.assembly.qr

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import xy.xy.base.assembly.base.BaseAssemblyViewWithContext

interface QrCreateAssemblyView :BaseAssemblyViewWithContext {
    fun createSaveView():View?
    fun createQrImageViewList():MutableList<ImageView?>
    fun createSaveQrButton():View?
    fun createChangeQrButton():View?
    fun createLogoBitmap():Bitmap?
    fun createNewContent():String?
    fun createBitmapSize():Int = 600
    fun createSaveHistoryKey():String?
}