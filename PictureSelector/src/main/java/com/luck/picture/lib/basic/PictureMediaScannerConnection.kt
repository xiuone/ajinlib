package com.luck.picture.lib.basic

import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.text.TextUtils
import com.luck.picture.lib.app.PictureAppMaster.Companion.instance
import com.luck.picture.lib.app.PictureAppMaster.appContext
import com.luck.picture.lib.app.PictureAppMaster.pictureSelectorEngine
import com.luck.picture.lib.PictureOnlyCameraFragment.Companion.newInstance
import com.luck.picture.lib.PictureOnlyCameraFragment.getFragmentTag
import com.luck.picture.lib.PictureSelectorFragment.getFragmentTag
import com.luck.picture.lib.PictureSelectorPreviewFragment.getFragmentTag
import com.luck.picture.lib.PictureSelectorPreviewFragment.Companion.newInstance
import com.luck.picture.lib.PictureSelectorPreviewFragment.setExternalPreviewData
import com.luck.picture.lib.PictureSelectorSystemFragment.Companion.newInstance
import com.luck.picture.lib.PictureSelectorFragment.Companion.newInstance
import androidx.fragment.app.FragmentActivity
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.config.SelectorProviders
import com.luck.picture.lib.utils.FileDirMap
import androidx.core.content.FileProvider
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

/**
 * @author：luck
 * @date：2019-12-03 10:41
 * @describe：刷新相册
 */
class PictureMediaScannerConnection : MediaScannerConnection.MediaScannerConnectionClient {
    interface ScanListener {
        fun onScanFinish()
    }

    private val mMs: MediaScannerConnection
    private val mPath: String
    private var mListener: ScanListener? = null

    constructor(context: Context, path: String, l: ScanListener?) {
        mListener = l
        mPath = path
        mMs = MediaScannerConnection(context.applicationContext, this)
        mMs.connect()
    }

    constructor(context: Context?, path: String) {
        mPath = path
        mMs = MediaScannerConnection(context!!.applicationContext, this)
        mMs.connect()
    }

    override fun onMediaScannerConnected() {
        if (!TextUtils.isEmpty(mPath)) {
            mMs.scanFile(mPath, null)
        }
    }

    override fun onScanCompleted(path: String, uri: Uri) {
        mMs.disconnect()
        if (mListener != null) {
            mListener!!.onScanFinish()
        }
    }
}