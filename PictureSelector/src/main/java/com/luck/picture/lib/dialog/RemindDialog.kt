package com.luck.picture.lib.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import com.luck.picture.lib.R
import com.luck.picture.lib.adapter.PictureAlbumAdapter.bindAlbumData
import com.luck.picture.lib.adapter.PictureAlbumAdapter.getAlbumList
import com.luck.picture.lib.adapter.PictureAlbumAdapter.setOnIBridgeAlbumWidget
import com.luck.picture.lib.config.SelectorConfig.selectCount
import com.luck.picture.lib.config.SelectorConfig.selectedResult
import com.luck.picture.lib.config.SelectorConfig

/**
 * @author：luck
 * @date：2021/11/19 5:11 下午
 * @describe：RemindDialog
 */
class RemindDialog constructor(context: Context?, tips: String?) : Dialog(
    (context)!!, R.style.Picture_Theme_Dialog
), View.OnClickListener {
    private val btnOk: TextView
    private val tvContent: TextView
    fun setButtonText(text: String?) {
        btnOk.setText(text)
    }

    fun setButtonTextColor(color: Int) {
        btnOk.setTextColor(color)
    }

    fun setContent(text: String?) {
        tvContent.setText(text)
    }

    fun setContentTextColor(color: Int) {
        tvContent.setTextColor(color)
    }

    private fun setDialogSize() {
        val params: WindowManager.LayoutParams = getWindow()!!.getAttributes()
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        params.gravity = Gravity.CENTER
        getWindow()!!.setWindowAnimations(R.style.PictureThemeDialogWindowStyle)
        getWindow()!!.setAttributes(params)
    }

    public override fun onClick(view: View) {
        val id: Int = view.getId()
        if (id == R.id.btnOk) {
            if (listener != null) {
                listener!!.onClick(view)
            } else {
                dismiss()
            }
        }
    }

    private var listener: OnDialogClickListener? = null
    fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

    open interface OnDialogClickListener {
        fun onClick(view: View?)
    }

    companion object {
        @Deprecated("")
        fun showTipsDialog(context: Context?, tips: String?): Dialog {
            return RemindDialog(context, tips)
        }

        fun buildDialog(context: Context?, tips: String?): RemindDialog {
            return RemindDialog(context, tips)
        }
    }

    init {
        setContentView(R.layout.ps_remind_dialog)
        btnOk = findViewById(R.id.btnOk)
        tvContent = findViewById(R.id.tv_content)
        tvContent.setText(tips)
        btnOk.setOnClickListener(this)
        setDialogSize()
    }
}