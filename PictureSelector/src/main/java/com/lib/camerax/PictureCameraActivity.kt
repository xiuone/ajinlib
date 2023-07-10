package com.lib.camerax

import android.Manifest
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.Toast
import com.lib.camerax.listener.IObtainCameraView
import com.lib.camerax.listener.CameraListener
import com.xy.base.act.ActivityBaseSwipeBack
import com.xy.base.utils.AndroidVersion
import com.xy.base.utils.exp.setSpBoolean
import com.xy.base.utils.exp.showToast

/**
 * @author：luck
 * @date：2021/11/29 7:50 下午
 * @describe：PictureCameraActivity
 */
class PictureCameraActivity : ActivityBaseSwipeBack(), IObtainCameraView {
    private val mCameraView by lazy { CustomCameraView(this) }

    private fun onStartCreate(){
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        if (AndroidVersion.isAndroid9()) {
            val lp = window.attributes
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = lp
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        onStartCreate()
        super.onCreate(savedInstanceState)

    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        onBackPressedHome = true
        mCameraView.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
        setContentView(mCameraView)
        mCameraView.post { mCameraView.setCameraConfig(intent) }
        mCameraView.setImageCallbackListener { url, imageView ->
            if (CustomCameraConfig.imageEngine != null) {
                CustomCameraConfig.imageEngine.loadImage(imageView.context, url, imageView)
            }
        }
        mCameraView.setCameraListener(ACameraListener())
        mCameraView.setOnCancelClickListener { handleCameraCancel() }
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mCameraView.onCancelMedia()
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mCameraView.onConfigurationChanged(newConfig)
    }

    override fun onBackPressed() {
        CustomCameraConfig.destroy()
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            finishAfterTransition()
        } else {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PermissionChecker.PERMISSION_SETTING_CODE) {
            if (PermissionChecker.checkSelfPermission(this, arrayOf(Manifest.permission.CAMERA))) {
                mCameraView!!.buildUseCameraCases()
            } else {
                SimpleXSpUtils.putBoolean(this, Manifest.permission.CAMERA, true)
                handleCameraCancel()
            }
        } else if (requestCode == PermissionChecker.PERMISSION_RECORD_AUDIO_SETTING_CODE) {
            if (!PermissionChecker.checkSelfPermission(this, arrayOf(Manifest.permission.RECORD_AUDIO))) {
                setSpBoolean(Manifest.permission.RECORD_AUDIO,true)
                showToast("Missing recording permission")
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (mPermissionResultCallback != null) {
            PermissionChecker.getInstance()
                .onRequestPermissionsResult(grantResults, mPermissionResultCallback)
            mPermissionResultCallback = null
        }
    }

    override fun getCustomCameraView(): ViewGroup = mCameraView

    override fun onDestroy() {
        mCameraView.onDestroy()
        super.onDestroy()
    }

    private inner class ACameraListener:CameraListener{
        override fun onPictureSuccess(url: String) = handleCameraSuccess()
        override fun onRecordSuccess(url: String) =handleCameraSuccess()
        override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) = showToast(message)
    }

}