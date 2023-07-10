package com.luck.picture.lib.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
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
 * @describe：PictureCommonDialog
 */
class PictureCommonDialog constructor(context: Context?, title: String?, content: String?) : Dialog(
    (context)!!, R.style.Picture_Theme_Dialog
), View.OnClickListener {
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
        if (id == R.id.btn_cancel) {
            dismiss()
        } else if (id == R.id.btn_commit) {
            dismiss()
            if (eventListener != null) {
                eventListener!!.onConfirm()
            }
        }
    }

    /**
     * 对外暴露的点击事件
     *
     * @param eventListener
     */
    fun setOnDialogEventListener(eventListener: OnDialogEventListener?) {
        this.eventListener = eventListener
    }

    private var eventListener: OnDialogEventListener? = null

    open interface OnDialogEventListener {
        fun onConfirm()
    }

    companion object {
        fun showDialog(context: Context?, title: String?, content: String?): PictureCommonDialog {
            val dialog: PictureCommonDialog = PictureCommonDialog(context, title, content)
            dialog.show()
            return dialog
        }
    }

    init {
        setContentView(R.layout.ps_common_dialog)
        val btnCancel: Button = findViewById(R.id.btn_cancel)
        val btnCommit: Button = findViewById(R.id.btn_commit)
        val tvTitle: TextView = findViewById(R.id.tvTitle)
        val tv_content: TextView = findViewById(R.id.tv_content)
        tvTitle.setText(title)
        tv_content.setText(content)
        btnCancel.setOnClickListener(this)
        btnCommit.setOnClickListener(this)
        setDialogSize()
    }
}