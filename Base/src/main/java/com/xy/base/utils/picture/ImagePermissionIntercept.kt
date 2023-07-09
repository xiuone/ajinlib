package com.xy.base.utils.picture

import android.Manifest
import android.content.Context
import android.text.SpannableString
import androidx.fragment.app.Fragment
import com.luck.picture.lib.interfaces.OnCallbackListener
import com.luck.picture.lib.interfaces.OnPermissionDeniedListener
import com.luck.picture.lib.interfaces.OnPermissionDescriptionListener
import com.xy.base.R
import com.xy.base.utils.exp.getResString
import com.xy.base.utils.exp.replaceContentColor

class ImagePermissionIntercept (): OnPermissionDescriptionListener ,OnPermissionDeniedListener{
//    private val descriptionDialog by lazy { this.listener?.onCreatePermissionDialogDescription() }
//    private val deniedDialog by lazy { this.listener?.onCreatePermissionDenied() }
    override fun onPermissionDescription(fragment: Fragment, permissionArray: Array<String>?) {
//        val context = listener?.getPageContext()?:return
//        descriptionDialog?.showDialog(addPermissionUseDescription(context,permissionArray))
    }

    override fun onDismiss(fragment: Fragment) {
//        descriptionDialog?.dismiss()
    }

    /**
     * 添加权限说明
     *
     * @param viewGroup
     * @param permissionArray
     */
    private fun addPermissionUseDescription(context: Context,permissionArray: Array<String>?):SpannableString? {
//        if (permissionArray == null || permissionArray.isEmpty())return null
//
//        val useTips = context.getResString(R.string.permission_use_tips)
//        val permissionCameraTitle = context.getResString(R.string.permission_camera)
//        val permissionMic = context.getResString(R.string.permission_microphone)
//        val permissionWrite = context.getResString(R.string.permission_write)
//
//        val permissionCameraDes = context.getResString(R.string.permission_camera_use_des)
//        val permissionMicDes = context.getResString(R.string.permission_microphone_use_des)
//        val permissionWriteDes = context.getResString(R.string.permission_write_use_des)
//
//        val stringBuffer = StringBuffer()
//        val usePermissionList = ArrayList<String>()
//        for (item in permissionArray){
//            if (item  == Manifest.permission.CAMERA){
//                if (!stringBuffer.isNullOrEmpty()){
//                    stringBuffer.append("\n")
//                }
//                stringBuffer.append(String.format(useTips,permissionCameraTitle,permissionCameraDes))
//                usePermissionList.add(permissionCameraTitle)
//            }
//            if (item  == Manifest.permission.RECORD_AUDIO){
//                if (!stringBuffer.isNullOrEmpty()){
//                    stringBuffer.append("\n")
//                }
//                stringBuffer.append(String.format(useTips,permissionMic,permissionMicDes))
//                usePermissionList.add(permissionCameraTitle)
//            }
//            if (item == Manifest.permission.WRITE_EXTERNAL_STORAGE || item == Manifest.permission.READ_EXTERNAL_STORAGE){
//                val permissionDes = String.format(useTips,permissionWrite,permissionWriteDes)
//                if (!stringBuffer.contains(permissionDes)) {
//                    if (!stringBuffer.isNullOrEmpty()){
//                        stringBuffer.append("\n")
//                    }
//                    stringBuffer.append(permissionDes)
//                    usePermissionList.add(permissionCameraTitle)
//                }
//            }
//        }
        val spannableString = SpannableString("")
//        val color = listener?.onCreatePermissionDialogDescriptionColor()?: Int.MAX_VALUE
//        if (Int.MAX_VALUE != color){
//            for (title in  usePermissionList){
//                spannableString.replaceContentColor(color,title)
//            }
//        }

        return spannableString
    }



    override fun onDenied(fragment: Fragment?, permissionArray: Array<String>?, requestCode: Int, call: OnCallbackListener<Boolean>?) {
//        deniedDialog?.showDialog(object : PermissionActionListener {
//            override fun onPermissionSureNextAction() {
//                super.onPermissionSureNextAction()
//                XXPermissions.startPermissionActivity(context, permissionArray)
//            } },addPermissionDeniedDescription(context,permissionArray))
    }
}
