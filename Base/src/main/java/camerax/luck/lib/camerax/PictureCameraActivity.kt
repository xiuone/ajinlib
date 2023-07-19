package camerax.luck.lib.camerax

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.KeyEvent
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import camerax.luck.lib.camerax.listener.CameraListener
import camerax.luck.lib.camerax.listener.ClickListener
import camerax.luck.lib.camerax.listener.ImageCallbackListener
import camerax.luck.lib.camerax.widget.CustomCameraView
import com.bumptech.glide.Glide
import xy.xy.base.act.ActivityBaseSwipeBack
import xy.xy.base.utils.exp.showToast

/**
 * @author：luck
 * @date：2021/11/29 7:50 下午
 * @describe：PictureCameraActivity
 */
class PictureCameraActivity : ActivityBaseSwipeBack() {
    private val mCameraView by lazy { CustomCameraView(this) }
    private val matchParent by lazy { RelativeLayout.LayoutParams.MATCH_PARENT }

    override fun startInitView(savedInstanceState: Bundle?) {
        super.startInitView(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val lp = window.attributes
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = lp
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        mCameraView.layoutParams = RelativeLayout.LayoutParams(matchParent,matchParent)
        setContentView(mCameraView)
        mCameraView.post { mCameraView.setCameraConfig(intent) }
    }

    override fun setListener() {
        super.setListener()
        mCameraView.setImageCallbackListener(object : ImageCallbackListener{
            override fun onLoadImage(url: String?, imageView: ImageView?) {
                imageView?.run {
                    Glide.with(applicationContext).load(url).into(imageView)
                }
            }
        })
        mCameraView.setCameraListener(object : CameraListener {
            override fun onPictureSuccess(url: String) = handleCameraSuccess()

            override fun onRecordSuccess(url: String) = handleCameraSuccess()

            override fun onError(videoCaptureError: Int, message: String?, cause: Throwable?) = showToast(message)
        })
        mCameraView.setOnCancelClickListener(object : ClickListener{
            override fun onClick() {
                handleCameraCancel()
            }

        })
    }

    private fun handleCameraSuccess() {
        val uri = intent.getParcelableExtra<Uri>(MediaStore.EXTRA_OUTPUT)
        val intent = Intent()
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        setResult(RESULT_OK, getIntent())
        onBackPressed()
    }

    private fun handleCameraCancel() {
        setResult(RESULT_CANCELED)
        onBackPressed()
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mCameraView.onConfigurationChanged(newConfig)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mCameraView.onCancelMedia()
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            finishAfterTransition()
        } else {
            super.onBackPressed()
        }
    }
}