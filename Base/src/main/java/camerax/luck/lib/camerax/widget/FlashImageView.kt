package camerax.luck.lib.camerax.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.camera.core.ImageCapture
import xy.xy.base.R

class FlashImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    AppCompatImageView(context, attrs, defStyleAttr){
    private var typeFlash = FlashType.TYPE_FLASH_OFF
    private var mImageCapture:ImageCapture?=null

    init {
        setOnClickListener{
            typeFlash = when(typeFlash){
                FlashType.TYPE_FLASH_AUTO-> FlashType.TYPE_FLASH_ON
                FlashType.TYPE_FLASH_ON-> FlashType.TYPE_FLASH_OFF
                FlashType.TYPE_FLASH_OFF-> FlashType.TYPE_FLASH_AUTO
            }
            setFlashMode()
        }
    }

    fun bindImageCapture(mImageCapture:ImageCapture?){
        this.mImageCapture = mImageCapture
    }

    /**
     * 闪光灯模式
     */
    fun setFlashMode() {
        when (typeFlash) {
            FlashType.TYPE_FLASH_AUTO -> {
                setImageResource(R.drawable.picture_ic_flash_auto)
                mImageCapture?.flashMode = ImageCapture.FLASH_MODE_AUTO
            }

            FlashType.TYPE_FLASH_ON -> {
                setImageResource(R.drawable.picture_ic_flash_on)
                mImageCapture?.flashMode = ImageCapture.FLASH_MODE_ON
            }

            FlashType.TYPE_FLASH_OFF -> {
                setImageResource(R.drawable.picture_ic_flash_off)
                mImageCapture?.flashMode = ImageCapture.FLASH_MODE_OFF
            }
        }
    }
}