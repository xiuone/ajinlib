package xy.xy.base.assembly.picture.select.audio

import android.Manifest
import android.content.Intent
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import com.hjq.permissions.IPermissionInterceptor
import com.hjq.permissions.XXPermissions
import com.luck.picture.lib.interfaces.OnRecordAudioInterceptListener
import xy.xy.base.utils.exp.showToast

open class RecordAudioIntercept(private val permissionInterceptor: IPermissionInterceptor?) : OnRecordAudioInterceptListener {

    override fun onRecordAudio(fragment: Fragment?, requestCode: Int) {
        val fragment = fragment?:return
        val context = fragment.context?:return
        if (XXPermissions.isGranted(context,Manifest.permission.RECORD_AUDIO)) {
            startRecordSoundAction(fragment, requestCode)
        } else if (permissionInterceptor != null){
            XXPermissions.with(context)
                .permission(Manifest.permission.RECORD_AUDIO)
                .interceptor(permissionInterceptor)
                .request { _, _ ->
                    startRecordSoundAction(fragment,
                        requestCode)
                }
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