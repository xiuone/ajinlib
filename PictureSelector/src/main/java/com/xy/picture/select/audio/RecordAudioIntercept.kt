package com.xy.picture.select.audio

import android.Manifest
import android.content.Intent
import android.provider.MediaStore
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.luck.picture.lib.interfaces.OnRecordAudioInterceptListener
import com.xy.base.permission.IPermissionInterceptor
import com.xy.base.permission.OnPermissionCallback
import com.xy.base.permission.XXPermissions
import com.xy.base.utils.exp.showToast

open class RecordAudioIntercept(private val permissionInterceptor: IPermissionInterceptor?) : OnRecordAudioInterceptListener{

    override fun onRecordAudio(fragment: Fragment?, requestCode: Int) {
        val fragment = fragment?:return
        val context = fragment.context?:return
        if (XXPermissions.isGranted(context,Manifest.permission.RECORD_AUDIO)) {
            startRecordSoundAction(fragment, requestCode)
        } else if (permissionInterceptor != null){
            XXPermissions.with(context,permissionInterceptor)
                .permission(Manifest.permission.RECORD_AUDIO)
                .request(object : OnPermissionCallback{
                    override fun onGranted(permissions: List<String?>, allGranted: Boolean) {
                        startRecordSoundAction(fragment,
                            requestCode)
                    }
                })
        }
    }

    /**
     * 启动录音意图
     *
     * @param fragment
     * @param requestCode
     */
    open fun startRecordSoundAction(fragment: Fragment?, requestCode: Int) {
        val fragment = fragment?:return
        val context = fragment.context
        val recordAudioIntent = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
        if (recordAudioIntent.resolveActivity(fragment.requireActivity().packageManager) != null) {
            fragment.startActivityForResult(recordAudioIntent, requestCode)
        } else {
            context?.showToast("The system is missing a recording component")
        }
    }
}