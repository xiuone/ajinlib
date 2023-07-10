package com.luck.picture.lib.dialog

import android.R
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.luck.picture.lib.adapter.PictureAlbumAdapter.bindAlbumData
import com.luck.picture.lib.adapter.PictureAlbumAdapter.getAlbumList
import com.luck.picture.lib.adapter.PictureAlbumAdapter.setOnIBridgeAlbumWidget
import com.luck.picture.lib.config.SelectorConfig.selectCount
import com.luck.picture.lib.config.SelectorConfig.selectedResult
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.interfaces.OnItemClickListener
import com.luck.picture.lib.utils.DensityUtil

/**
 * @author：luck
 * @date：2019-12-12 16:39
 * @describe：PhotoSelectedDialog
 */
class PhotoItemSelectedDialog constructor() : DialogFragment(), View.OnClickListener {
    private var isCancel: Boolean = true
    public override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (getDialog() != null) {
            getDialog()!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            if (getDialog()!!.getWindow() != null) {
                getDialog()!!.getWindow()!!.setBackgroundDrawableResource(R.color.transparent)
            }
        }
        return inflater.inflate(com.luck.picture.lib.R.layout.ps_dialog_camera_selected, container)
    }

    public override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvPicturePhoto: TextView = view.findViewById(com.luck.picture.lib.R.id.ps_tv_photo)
        val tvPictureVideo: TextView = view.findViewById(com.luck.picture.lib.R.id.ps_tv_video)
        val tvPictureCancel: TextView = view.findViewById(com.luck.picture.lib.R.id.ps_tv_cancel)
        tvPictureVideo.setOnClickListener(this)
        tvPicturePhoto.setOnClickListener(this)
        tvPictureCancel.setOnClickListener(this)
    }

    public override fun onStart() {
        super.onStart()
        initDialogStyle()
    }

    /**
     * DialogFragment Style
     */
    private fun initDialogStyle() {
        val dialog: Dialog? = getDialog()
        if (dialog != null) {
            val window: Window? = dialog.getWindow()
            if (window != null) {
                window.setLayout(
                    DensityUtil.getRealScreenWidth(getContext()),
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                window.setGravity(Gravity.BOTTOM)
                window.setWindowAnimations(com.luck.picture.lib.R.style.PictureThemeDialogFragmentAnim)
            }
        }
    }

    private var onItemClickListener: OnItemClickListener? = null
    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    private var onDismissListener: OnDismissListener? = null
    fun setOnDismissListener(listener: OnDismissListener?) {
        onDismissListener = listener
    }

    open interface OnDismissListener {
        fun onDismiss(isCancel: Boolean, dialog: DialogInterface?)
    }

    public override fun onClick(v: View) {
        val id: Int = v.getId()
        if (onItemClickListener != null) {
            if (id == com.luck.picture.lib.R.id.ps_tv_photo) {
                onItemClickListener!!.onItemClick(v, IMAGE_CAMERA)
                isCancel = false
            } else if (id == com.luck.picture.lib.R.id.ps_tv_video) {
                onItemClickListener!!.onItemClick(v, VIDEO_CAMERA)
                isCancel = false
            }
        }
        dismissAllowingStateLoss()
    }

    public override fun show(manager: FragmentManager, tag: String?) {
        val ft: FragmentTransaction = manager.beginTransaction()
        ft.add(this, tag)
        ft.commitAllowingStateLoss()
    }

    public override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (onDismissListener != null) {
            onDismissListener!!.onDismiss(isCancel, dialog)
        }
    }

    companion object {
        val IMAGE_CAMERA: Int = 0
        val VIDEO_CAMERA: Int = 1
        fun newInstance(): PhotoItemSelectedDialog {
            return PhotoItemSelectedDialog()
        }
    }
}