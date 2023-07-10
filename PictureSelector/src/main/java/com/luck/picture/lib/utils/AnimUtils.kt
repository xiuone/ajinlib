package com.luck.picture.lib.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import com.luck.picture.lib.config.PictureMimeType.isContent
import com.luck.picture.lib.basic.PictureContentResolver.openInputStream
import com.luck.picture.lib.basic.PictureContentResolver.openOutputStream
import com.luck.picture.lib.immersive.RomUtils.isSamsung
import com.luck.picture.lib.thread.PictureThreadUtils.executeByIo
import com.luck.picture.lib.config.PictureMimeType.isHasAudio
import com.luck.picture.lib.config.PictureMimeType.isHasVideo
import com.luck.picture.lib.config.PictureMimeType.isHasGif
import com.luck.picture.lib.config.PictureMimeType.isUrlHasGif
import com.luck.picture.lib.config.PictureMimeType.isHasHttp
import com.luck.picture.lib.thread.PictureThreadUtils.cancel
import com.luck.picture.lib.interfaces.OnCallbackListener.onCall
import com.luck.picture.lib.config.PictureMimeType.isHasImage
import com.luck.picture.lib.app.PictureAppMaster.Companion.instance
import com.luck.picture.lib.app.PictureAppMaster.appContext
import com.luck.picture.lib.config.SelectMimeType.ofImage
import com.luck.picture.lib.config.PictureMimeType.getLastSourceSuffix
import com.luck.picture.lib.thread.PictureThreadUtils.isInUiThread
import com.luck.picture.lib.thread.PictureThreadUtils.runOnUiThread
import androidx.fragment.app.FragmentActivity
import com.luck.picture.lib.utils.FileDirMap
import com.luck.picture.lib.config.SelectorConfig
import androidx.core.content.FileProvider
import kotlin.jvm.JvmOverloads
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeCompat

/**
 * @author：luck
 * @date：2019-11-21 19:20
 * @describe：动画相关
 */
object AnimUtils {
    const val DURATION = 250

    /**
     * 箭头旋转动画
     *
     * @param arrow
     * @param isFlag
     */
    fun rotateArrow(arrow: ImageView?, isFlag: Boolean) {
        val srcValue: Float
        val targetValue: Float
        if (isFlag) {
            srcValue = 0f
            targetValue = 180f
        } else {
            srcValue = 180f
            targetValue = 0f
        }
        val objectAnimator = ObjectAnimator.ofFloat(arrow, "rotation", srcValue, targetValue)
        objectAnimator.duration = DURATION.toLong()
        objectAnimator.interpolator = LinearInterpolator()
        objectAnimator.start()
    }

    /**
     * 缩放动画
     *
     * @param view
     */
    fun selectZoom(view: View?) {
        val animatorSet = AnimatorSet()
        val objectAnimatorX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 1.05f, 1.0f)
        val objectAnimatorY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 1.05f, 1.0f)
        animatorSet.playTogether(objectAnimatorX, objectAnimatorY)
        animatorSet.duration = DURATION.toLong()
        animatorSet.interpolator = LinearInterpolator()
        animatorSet.start()
    }
}