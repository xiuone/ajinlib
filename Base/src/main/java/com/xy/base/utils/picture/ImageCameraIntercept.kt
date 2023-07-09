package com.xy.base.utils.picture

import android.Manifest
import android.content.Intent
import android.provider.MediaStore
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.luck.picture.lib.interfaces.OnCameraInterceptListener
import com.luck.picture.lib.interfaces.OnRecordAudioInterceptListener
import com.luck.picture.lib.permissions.PermissionChecker
import com.luck.picture.lib.permissions.PermissionResultCallback
import com.luck.picture.lib.utils.ToastUtils

class ImageCameraIntercept : OnRecordAudioInterceptListener, OnCameraInterceptListener {

    override fun onRecordAudio(fragment: Fragment?, requestCode: Int) {
        val recordAudio = arrayOf(Manifest.permission.RECORD_AUDIO)
        if (PermissionChecker.isCheckSelfPermission(fragment?.context, recordAudio)) {
            startRecordSoundAction(fragment, requestCode)
        } else {
            addPermissionDescription(false, fragment?.requireView() as ViewGroup, recordAudio)
            PermissionChecker.getInstance().requestPermissions(fragment,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                object : PermissionResultCallback {
                    override fun onGranted() {
//                        removePermissionDescription(fragment!!.requireView() as ViewGroup)
                        startRecordSoundAction(fragment,
                            requestCode)
                    }

                    override fun onDenied() {
//                        removePermissionDescription(fragment!!.requireView() as ViewGroup)
                    }
                })
        }
    }

    override fun openCamera(fragment: Fragment?, cameraMode: Int, requestCode: Int) {
//        val fragment = fragment?:return
//        val camera = SimpleCameraX.of()
//        camera.isAutoRotation(true)
//        camera.setCameraMode(cameraMode)
//        camera.setVideoFrameRate(25)
//        camera.setVideoBitRate(3 * 1024 * 1024)
//        camera.isDisplayRecordChangeTime(true)
//        camera.isManualFocusCameraPreview(true)
//        camera.isZoomCameraPreview(true)
//        camera.setOutputPathDir(getSandboxCameraOutputPath())
//        camera.setPermissionDeniedListener(getSimpleXPermissionDeniedListener())
//        camera.setPermissionDescriptionListener(getSimpleXPermissionDescriptionListener())
//        camera.setImageEngine { context, url, imageView ->
//            Glide.with(context).load(url).into(imageView)
//        }
//        camera.start(fragment.requireActivity(), fragment, requestCode)
    }


    /**
     * 启动录音意图
     *
     * @param fragment
     * @param requestCode
     */
    private fun startRecordSoundAction(fragment: Fragment?, requestCode: Int) {
        val recordAudioIntent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
        val packageManager = fragment?.requireActivity()?.packageManager
        if (packageManager != null && recordAudioIntent.resolveActivity(packageManager) != null) {
            fragment.startActivityForResult(recordAudioIntent, requestCode)
        } else {
            ToastUtils.showToast(fragment?.context, "The system is missing a recording component")
        }
    }

    /**
     * 添加权限说明
     *
     * @param viewGroup
     * @param permissionArray
     */
    private fun addPermissionDescription(isHasSimpleXCamera: Boolean, viewGroup: ViewGroup, permissionArray: Array<String> ) {
//        val dp10 = DensityUtil.dip2px(viewGroup.context, 10f)
//        val dp15 = DensityUtil.dip2px(viewGroup.context, 15f)
//        val view = MediumBoldTextView(viewGroup.context)
//        view.tag = com.luck.pictureselector.MainActivity.TAG_EXPLAIN_VIEW
//        view.textSize = 14f
//        view.setTextColor(Color.parseColor("#333333"))
//        view.setPadding(dp10, dp15, dp10, dp15)
//        val title: String
//        val explain: String
//        if (TextUtils.equals(permissionArray[0], PermissionConfig.CAMERA[0])) {
//            title = "相机权限使用说明"
//            explain = "相机权限使用说明\n用户app用于拍照/录视频"
//        } else if (TextUtils.equals(permissionArray[0], Manifest.permission.RECORD_AUDIO)) {
//            if (isHasSimpleXCamera) {
//                title = "麦克风权限使用说明"
//                explain = "麦克风权限使用说明\n用户app用于录视频时采集声音"
//            } else {
//                title = "录音权限使用说明"
//                explain = "录音权限使用说明\n用户app用于采集声音"
//            }
//        } else {
//            title = "存储权限使用说明"
//            explain = "存储权限使用说明\n用户app写入/下载/保存/读取/修改/删除图片、视频、文件等信息"
//        }
//        val startIndex = 0
//        val endOf = startIndex + title.length
//        val builder = SpannableStringBuilder(explain)
//        builder.setSpan(AbsoluteSizeSpan(DensityUtil.dip2px(viewGroup.context, 16f)),
//            startIndex,
//            endOf,
//            Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
//        builder.setSpan(ForegroundColorSpan(-0xcccccd),
//            startIndex,
//            endOf,
//            Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
//        view.text = builder
//        view.background =
//            ContextCompat.getDrawable(viewGroup.context, R.drawable.ps_demo_permission_desc_bg)
//        if (isHasSimpleXCamera) {
//            val layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
//                RelativeLayout.LayoutParams.WRAP_CONTENT)
//            layoutParams.topMargin = DensityUtil.getStatusBarHeight(viewGroup.context)
//            layoutParams.leftMargin = dp10
//            layoutParams.rightMargin = dp10
//            viewGroup.addView(view, layoutParams)
//        } else {
//            val layoutParams =
//                ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
//                    ConstraintLayout.LayoutParams.WRAP_CONTENT)
//            layoutParams.topToBottom = R.id.title_bar
//            layoutParams.leftToLeft = ConstraintSet.PARENT_ID
//            layoutParams.leftMargin = dp10
//            layoutParams.rightMargin = dp10
//            viewGroup.addView(view, layoutParams)
//        }
    }
}