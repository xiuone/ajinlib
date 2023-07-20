package camerax.luck.lib.camerax.widget.camera

import android.content.Context
import android.view.OrientationEventListener
import android.view.Surface

/**
 * @author：luck
 * @date：2022/6/4 3:28 下午
 * @describe：CameraXOrientationEventListener
 */
class OrientationEventListener(context: Context?, private val changedListener: OnOrientationChangedListener?) :
    OrientationEventListener(context) {
    private var mRotation = Surface.ROTATION_0

    override fun onOrientationChanged(orientation: Int) {
        if (orientation == ORIENTATION_UNKNOWN) {
            return
        }
        val currentRotation: Int = if (orientation in 81..99) {
            Surface.ROTATION_270
        } else if (orientation in 171..189) {
            Surface.ROTATION_180
        } else if (orientation in 261..279) {
            Surface.ROTATION_90
        } else {
            Surface.ROTATION_0
        }
        if (mRotation != currentRotation) {
            mRotation = currentRotation
            changedListener?.onOrientationChanged(mRotation)
        }
    }

    fun star()  = enable()

    fun stop() = disable()

    interface OnOrientationChangedListener {
        fun onOrientationChanged(orientation: Int)
    }
}