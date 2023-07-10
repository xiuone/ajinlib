package com.luck.picture.lib.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import com.luck.picture.lib.R
import com.luck.picture.lib.adapter.PictureAlbumAdapter.bindAlbumData
import com.luck.picture.lib.adapter.PictureAlbumAdapter.getAlbumList
import com.luck.picture.lib.adapter.PictureAlbumAdapter.setOnIBridgeAlbumWidget
import com.luck.picture.lib.config.SelectorConfig.selectCount
import com.luck.picture.lib.config.SelectorConfig.selectedResult
import com.luck.picture.lib.config.SelectorConfig

class PictureLoadingDialog constructor(context: Context?) : Dialog(
    (context)!!, R.style.Picture_Theme_AlertDialog
) {
    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ps_alert_dialog)
        setDialogSize()
    }

    private fun setDialogSize() {
        val params: WindowManager.LayoutParams = getWindow()!!.getAttributes()
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        params.gravity = Gravity.CENTER
        getWindow()!!.setWindowAnimations(R.style.PictureThemeDialogWindowStyle)
        getWindow()!!.setAttributes(params)
    }

    init {
        setCancelable(true)
        setCanceledOnTouchOutside(false)
    }
}