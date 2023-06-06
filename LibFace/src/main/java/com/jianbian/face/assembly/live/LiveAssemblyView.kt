package com.jianbian.face.assembly.live

import com.jianbian.face.view.AnimImageView
import com.jianbian.face.view.SoundImageView
import com.jianbian.face.view.detect.FaceDetectRoundView
import com.xy.base.assembly.base.BaseAssemblyView
import com.xy.base.dialog.listener.DialogCancelSureView
import com.xy.base.dialog.listener.DialogImplListener
import java.util.ArrayList

interface LiveAssemblyView :BaseAssemblyView,DialogImplListener,DialogCancelSureView{
    fun onCreateFaceDetectRoundView(): FaceDetectRoundView?
    fun onCreateSoundView(): SoundImageView?
    fun onCreateAnimView(): AnimImageView?
    fun onGetLiveList(data: ArrayList<String>)
    fun onRecollect()
    fun onReturn()
}