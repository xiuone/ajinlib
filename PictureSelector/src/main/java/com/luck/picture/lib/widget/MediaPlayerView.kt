package com.luck.picture.lib.widget

import android.content.Context
import android.graphics.PixelFormat
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.util.AttributeSet
import android.view.Gravity
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.FrameLayout
import com.luck.picture.lib.config.SelectorProviders.Companion.instance
import com.luck.picture.lib.config.SelectorProviders.selectorConfig
import com.luck.picture.lib.config.SelectorConfig.selectCount
import com.luck.picture.lib.style.PictureSelectorStyle.bottomBarStyle
import com.luck.picture.lib.style.BottomNavBarStyle.bottomOriginalDrawableLeft
import com.luck.picture.lib.utils.StyleUtils.checkStyleValidity
import com.luck.picture.lib.style.BottomNavBarStyle.bottomOriginalTextResId
import com.luck.picture.lib.style.BottomNavBarStyle.bottomOriginalText
import com.luck.picture.lib.utils.StyleUtils.checkTextValidity
import com.luck.picture.lib.style.BottomNavBarStyle.bottomOriginalTextSize
import com.luck.picture.lib.utils.StyleUtils.checkSizeValidity
import com.luck.picture.lib.style.BottomNavBarStyle.bottomOriginalTextColor
import com.luck.picture.lib.style.BottomNavBarStyle.bottomNarBarHeight
import com.luck.picture.lib.utils.DensityUtil.dip2px
import com.luck.picture.lib.style.BottomNavBarStyle.bottomNarBarBackgroundColor
import com.luck.picture.lib.style.BottomNavBarStyle.bottomPreviewNormalTextColor
import com.luck.picture.lib.style.BottomNavBarStyle.bottomPreviewNormalTextSize
import com.luck.picture.lib.style.BottomNavBarStyle.bottomPreviewNormalTextResId
import com.luck.picture.lib.style.BottomNavBarStyle.bottomPreviewNormalText
import com.luck.picture.lib.style.BottomNavBarStyle.bottomEditorTextResId
import com.luck.picture.lib.style.BottomNavBarStyle.bottomEditorText
import com.luck.picture.lib.style.BottomNavBarStyle.bottomEditorTextSize
import com.luck.picture.lib.style.BottomNavBarStyle.bottomEditorTextColor
import com.luck.picture.lib.style.BottomNavBarStyle.bottomPreviewSelectTextColor
import com.luck.picture.lib.style.BottomNavBarStyle.bottomPreviewSelectTextResId
import com.luck.picture.lib.style.BottomNavBarStyle.bottomPreviewSelectText
import com.luck.picture.lib.utils.StyleUtils.checkTextFormatValidity
import com.luck.picture.lib.config.SelectorConfig.selectedResult
import com.luck.picture.lib.utils.PictureFileUtils.formatAccurateUnitFileSize
import com.luck.picture.lib.style.PictureSelectorStyle.selectMainStyle
import com.luck.picture.lib.style.SelectMainStyle.selectNormalBackgroundResources
import com.luck.picture.lib.style.SelectMainStyle.selectNormalTextResId
import com.luck.picture.lib.style.SelectMainStyle.selectNormalText
import com.luck.picture.lib.utils.StyleUtils.checkTextTwoFormatValidity
import com.luck.picture.lib.style.SelectMainStyle.selectNormalTextSize
import com.luck.picture.lib.style.SelectMainStyle.selectNormalTextColor
import com.luck.picture.lib.style.BottomNavBarStyle.isCompleteCountTips
import com.luck.picture.lib.style.BottomNavBarStyle.bottomSelectNumResources
import com.luck.picture.lib.style.BottomNavBarStyle.bottomSelectNumTextSize
import com.luck.picture.lib.style.BottomNavBarStyle.bottomSelectNumTextColor
import com.luck.picture.lib.style.SelectMainStyle.selectBackgroundResources
import com.luck.picture.lib.style.SelectMainStyle.selectTextResId
import com.luck.picture.lib.style.SelectMainStyle.selectText
import com.luck.picture.lib.style.SelectMainStyle.selectTextSize
import com.luck.picture.lib.style.SelectMainStyle.selectTextColor
import com.luck.picture.lib.utils.ValueOf.toString
import com.luck.picture.lib.interfaces.OnSelectAnimListener.onSelectAnim
import com.luck.picture.lib.style.SelectMainStyle.isCompleteSelectRelativeTop
import com.luck.picture.lib.config.PictureMimeType.isContent
import com.luck.picture.lib.style.BottomNavBarStyle.bottomPreviewNarBarBackgroundColor
import com.luck.picture.lib.style.PictureSelectorStyle.titleBarStyle
import com.luck.picture.lib.style.TitleBarStyle.previewTitleBackgroundColor
import com.luck.picture.lib.style.TitleBarStyle.titleBackgroundColor
import com.luck.picture.lib.style.TitleBarStyle.previewTitleLeftBackResource
import com.luck.picture.lib.interfaces.OnRecyclerViewPreloadMoreListener.onRecyclerViewPreloadMore
import com.luck.picture.lib.interfaces.OnRecyclerViewScrollListener.onScrolled
import com.luck.picture.lib.interfaces.OnRecyclerViewScrollStateListener.onScrollSlow
import com.luck.picture.lib.interfaces.OnRecyclerViewScrollStateListener.onScrollFast
import com.luck.picture.lib.interfaces.OnRecyclerViewScrollListener.onScrollStateChanged
import com.luck.picture.lib.config.SelectMimeType.ofAudio
import com.luck.picture.lib.utils.DensityUtil.getStatusBarHeight
import com.luck.picture.lib.style.TitleBarStyle.titleBarHeight
import com.luck.picture.lib.style.TitleBarStyle.isDisplayTitleBarLine
import com.luck.picture.lib.style.TitleBarStyle.titleBarLineColor
import com.luck.picture.lib.style.TitleBarStyle.titleLeftBackResource
import com.luck.picture.lib.style.TitleBarStyle.titleDefaultTextResId
import com.luck.picture.lib.style.TitleBarStyle.titleDefaultText
import com.luck.picture.lib.style.TitleBarStyle.titleTextSize
import com.luck.picture.lib.style.TitleBarStyle.titleTextColor
import com.luck.picture.lib.style.TitleBarStyle.titleDrawableRightResource
import com.luck.picture.lib.style.TitleBarStyle.titleAlbumBackgroundResource
import com.luck.picture.lib.style.TitleBarStyle.isHideCancelButton
import com.luck.picture.lib.style.TitleBarStyle.titleCancelBackgroundResource
import com.luck.picture.lib.style.TitleBarStyle.titleCancelTextResId
import com.luck.picture.lib.style.TitleBarStyle.titleCancelText
import com.luck.picture.lib.style.TitleBarStyle.titleCancelTextColor
import com.luck.picture.lib.style.TitleBarStyle.titleCancelTextSize
import com.luck.picture.lib.style.TitleBarStyle.previewDeleteBackgroundResource
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.config.SelectorProviders
import androidx.core.content.ContextCompat
import kotlin.jvm.JvmOverloads
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.ViewCompat
import java.io.IOException

/**
 * @author：luck
 * @date：2022/7/1 5:10 下午
 * @describe：MediaPlayerView
 */
class MediaPlayerView : FrameLayout, SurfaceHolder.Callback {
    var mediaPlayer: MediaPlayer? = null
        private set
    var surfaceView: VideoSurfaceView? = null
        private set

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        surfaceView = VideoSurfaceView(context)
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        layoutParams.gravity = Gravity.CENTER
        surfaceView!!.layoutParams = layoutParams
        addView(surfaceView)
        val surfaceHolder = surfaceView!!.holder
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT)
        surfaceHolder.addCallback(this)
    }

    fun initMediaPlayer(): MediaPlayer {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
        }
        mediaPlayer!!.setOnVideoSizeChangedListener { mediaPlayer, width, height ->
            surfaceView!!.adjustVideoSize(
                mediaPlayer.videoWidth,
                mediaPlayer.videoHeight
            )
        }
        return mediaPlayer
    }

    fun start(path: String?) {
        try {
            if (isContent(path!!)) {
                mediaPlayer!!.setDataSource(context, Uri.parse(path))
            } else {
                mediaPlayer!!.setDataSource(path)
            }
            mediaPlayer!!.prepareAsync()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer!!.setDisplay(holder)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
    override fun surfaceDestroyed(holder: SurfaceHolder) {}
    fun clearCanvas() {
        surfaceView!!.holder.setFormat(PixelFormat.OPAQUE)
        surfaceView!!.holder.setFormat(PixelFormat.TRANSPARENT)
    }

    class VideoSurfaceView @JvmOverloads constructor(
        context: Context?,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    ) : SurfaceView(context, attrs, defStyleAttr) {
        /**
         * 视频宽度
         */
        private var videoWidth = 0

        /**
         * 视频高度
         */
        private var videoHeight = 0
        fun adjustVideoSize(videoWidth: Int, videoHeight: Int) {
            if (videoWidth == 0 || videoHeight == 0) {
                return
            }
            this.videoWidth = videoWidth
            this.videoHeight = videoHeight
            holder.setFixedSize(videoWidth, videoHeight)
            requestLayout()
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            var width = getDefaultSize(videoWidth, widthMeasureSpec)
            var height = getDefaultSize(videoHeight, heightMeasureSpec)
            if (videoWidth > 0 && videoHeight > 0) {
                val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
                val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
                val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
                val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
                if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY) {
                    width = widthSpecSize
                    height = heightSpecSize
                    if (videoWidth * height < width * videoHeight) {
                        width = height * videoWidth / videoHeight
                    } else if (videoWidth * height > width * videoHeight) {
                        height = width * videoHeight / videoWidth
                    }
                } else if (widthSpecMode == MeasureSpec.EXACTLY) {
                    width = widthSpecSize
                    height = width * videoHeight / videoWidth
                    if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                        height = heightSpecSize
                    }
                } else if (heightSpecMode == MeasureSpec.EXACTLY) {
                    height = heightSpecSize
                    width = height * videoWidth / videoHeight
                    if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                        width = widthSpecSize
                    }
                } else {
                    width = videoWidth
                    height = videoHeight
                    if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                        height = heightSpecSize
                        width = height * videoWidth / videoHeight
                    }
                    if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                        width = widthSpecSize
                        height = width * videoHeight / videoWidth
                    }
                }
            }
            setMeasuredDimension(width, height)
        }
    }

    fun release() {
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
            mediaPlayer!!.setOnPreparedListener(null)
            mediaPlayer!!.setOnCompletionListener(null)
            mediaPlayer!!.setOnErrorListener(null)
            mediaPlayer = null
        }
    }
}