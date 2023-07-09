package com.xy.base.utils.exp

import android.Manifest
import android.app.Activity
import android.content.*
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.text.SpannableString
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Space
import android.widget.Toast
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.xy.base.R
import com.xy.base.permission.XXPermissions
import com.xy.base.utils.Logger
import com.xy.base.utils.runMain
import java.io.File


/**
 * 添加权限说明
 *
 * @param viewGroup
 * @param permissionArray
 */
fun Context?.addPermissionUseDescription(permissionArray: ArrayList<String>,color:Int): SpannableString? {
    if (permissionArray.isNullOrEmpty() || this == null)return null
    val useTips = getResString(R.string.permission_use_tips)
    val permissionCameraTitle = getResString(R.string.permission_camera)
    val permissionMic = getResString(R.string.permission_microphone)
    val permissionWrite = getResString(R.string.permission_write)

    val permissionCameraDes =getResString(R.string.permission_camera_use_des)
    val permissionMicDes = getResString(R.string.permission_microphone_use_des)
    val permissionWriteDes = getResString(R.string.permission_write_use_des)

    val stringBuffer = StringBuffer()
    val usePermissionList = ArrayList<String>()
    for (item in permissionArray){
        if (item  == Manifest.permission.CAMERA){
            if (!stringBuffer.isNullOrEmpty()){
                stringBuffer.append("\n")
            }
            stringBuffer.append(String.format(useTips,permissionCameraTitle,permissionCameraDes))
            usePermissionList.add(permissionCameraTitle)
        }
        if (item  == Manifest.permission.RECORD_AUDIO){
            if (!stringBuffer.isNullOrEmpty()){
                stringBuffer.append("\n")
            }
            stringBuffer.append(String.format(useTips,permissionMic,permissionMicDes))
            usePermissionList.add(permissionCameraTitle)
        }
        if (item == Manifest.permission.WRITE_EXTERNAL_STORAGE || item == Manifest.permission.READ_EXTERNAL_STORAGE){
            val permissionDes = String.format(useTips,permissionWrite,permissionWriteDes)
            if (!stringBuffer.contains(permissionDes)) {
                if (!stringBuffer.isNullOrEmpty()){
                    stringBuffer.append("\n")
                }
                stringBuffer.append(permissionDes)
                usePermissionList.add(permissionCameraTitle)
            }
        }
    }
    val spannableString = SpannableString(stringBuffer.toString())
    for (title in  usePermissionList){
        spannableString.replaceContentColor(color,title)
    }
    return spannableString
}

/**
 * 添加权限说明
 *
 * @param viewGroup
 * @param permissionArray
 */
fun Context?.addPermissionDeniedDescription(permissionArray: ArrayList<String>,color:Int): SpannableString? {
    if (permissionArray.isNullOrEmpty() || this == null)return null

    val useTips = getResString(R.string.permission_denied_tips)
    val permissionCameraTitle = getResString(R.string.permission_camera)
    val permissionMic = getResString(R.string.permission_microphone)
    val permissionWrite = getResString(R.string.permission_write)

    val permissionCameraDes = getResString(R.string.permission_camera_denied_des)
    val permissionMicDes = getResString(R.string.permission_microphone_denied_des)
    val permissionWriteDes = getResString(R.string.permission_write_denied_des)

    val stringBuffer = StringBuffer()
    val usePermissionList = ArrayList<String>()
    for (item in permissionArray){
        if (item  == Manifest.permission.CAMERA){
            if (!stringBuffer.isNullOrEmpty()){
                stringBuffer.append("\n")
            }
            stringBuffer.append(String.format(useTips,permissionCameraTitle,permissionCameraDes))
            usePermissionList.add(permissionCameraTitle)
        }
        if (item  == Manifest.permission.RECORD_AUDIO){
            if (!stringBuffer.isNullOrEmpty()){
                stringBuffer.append("\n")
            }
            stringBuffer.append(String.format(useTips,permissionMic,permissionMicDes))
            usePermissionList.add(permissionCameraTitle)
        }
        if (item == Manifest.permission.WRITE_EXTERNAL_STORAGE || item == Manifest.permission.READ_EXTERNAL_STORAGE){
            val permissionDes = String.format(useTips,permissionWrite,permissionWriteDes)
            if (!stringBuffer.contains(permissionDes)) {
                if (!stringBuffer.isNullOrEmpty()){
                    stringBuffer.append("\n")
                }
                stringBuffer.append(permissionDes)
                usePermissionList.add(permissionCameraTitle)
            }
        }
    }
    val spannableString = SpannableString(stringBuffer.toString())
    for (title in  usePermissionList){
        spannableString.replaceContentColor(color,title)
    }
    return spannableString
}

