package com.luck.picture.lib.adapter.holder

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.config.SelectorProviders
import com.luck.picture.lib.utils.IntentUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.luck.picture.lib.R
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.engine.MediaPlayerEngine
import com.luck.picture.lib.engine.VideoPlayerEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnPlayerListener
import java.lang.NullPointerException

/**
 * @author：luck
 * @date：2021/12/15 5:12 下午
 * @describe：PreviewVideoHolder
 */
class PreviewVideoHolder(itemView: View) : BasePreviewHolder(itemView) {
    @JvmField
    var ivPlayButton: ImageView
    var progress: ProgressBar
    var videoPlayer: View?
    private var isPlayed = false
    override fun findViews(itemView: View?) {}
    override fun loadImage(media: LocalMedia, maxWidth: Int, maxHeight: Int) {
        if (selectorConfig.imageEngine != null) {
            val availablePath = media.availablePath
            if (maxWidth == PictureConfig.UNSET && maxHeight == PictureConfig.UNSET) {
                selectorConfig.imageEngine.loadImage(
                    itemView.context,
                    availablePath,
                    coverImageView
                )
            } else {
                selectorConfig.imageEngine.loadImage(
                    itemView.context,
                    coverImageView,
                    availablePath,
                    maxWidth,
                    maxHeight
                )
            }
        }
    }

    override fun onClickBackPressed() {
        coverImageView.setOnViewTapListener { view, x, y ->
            if (mPreviewEventListener != null) {
                mPreviewEventListener!!.onBackPressed()
            }
        }
    }

    override fun onLongPressDownload(media: LocalMedia?) {
        coverImageView.setOnLongClickListener {
            if (mPreviewEventListener != null) {
                mPreviewEventListener!!.onLongPressDownload(media)
            }
            false
        }
    }

    override fun bindData(media: LocalMedia, position: Int) {
        super.bindData(media, position)
        setScaleDisplaySize(media)
        ivPlayButton.setOnClickListener {
            if (selectorConfig.isPauseResumePlay) {
                dispatchPlay()
            } else {
                startPlay()
            }
        }
        itemView.setOnClickListener {
            if (selectorConfig.isPauseResumePlay) {
                dispatchPlay()
            } else {
                if (mPreviewEventListener != null) {
                    mPreviewEventListener!!.onBackPressed()
                }
            }
        }
    }

    /**
     * 视频播放状态分发
     */
    private fun dispatchPlay() {
        if (isPlayed) {
            if (isPlaying) {
                onPause()
            } else {
                onResume()
            }
        } else {
            startPlay()
        }
    }

    /**
     * 恢复播放
     */
    private fun onResume() {
        ivPlayButton.visibility = View.GONE
        if (selectorConfig.videoPlayerEngine != null) {
            selectorConfig.videoPlayerEngine.onResume(videoPlayer)
        }
    }

    /**
     * 暂停播放
     */
    fun onPause() {
        ivPlayButton.visibility = View.VISIBLE
        if (selectorConfig.videoPlayerEngine != null) {
            selectorConfig.videoPlayerEngine.onPause(videoPlayer)
        }
    }

    /**
     * 是否正在播放中
     */
    override val isPlaying: Boolean
        get() = (selectorConfig.videoPlayerEngine != null
                && selectorConfig.videoPlayerEngine.isPlaying(videoPlayer))

    /**
     * 外部播放状态监听回调
     */
    private val mPlayerListener: OnPlayerListener = object : OnPlayerListener {
        override fun onPlayerError() {
            playerDefaultUI()
        }

        override fun onPlayerReady() {
            playerIngUI()
        }

        override fun onPlayerLoading() {
            progress.visibility = View.VISIBLE
        }

        override fun onPlayerEnd() {
            playerDefaultUI()
        }
    }

    /**
     * 开始播放视频
     */
    fun startPlay() {
        if (selectorConfig.isUseSystemVideoPlayer) {
            IntentUtils.startSystemPlayerVideo(itemView.context, media!!.availablePath)
        } else {
            if (videoPlayer == null) {
                throw NullPointerException("VideoPlayer cannot be empty,Please implement " + VideoPlayerEngine::class.java)
            }
            if (selectorConfig.videoPlayerEngine != null) {
                progress.visibility = View.VISIBLE
                ivPlayButton.visibility = View.GONE
                mPreviewEventListener!!.onPreviewVideoTitle(media!!.fileName)
                isPlayed = true
                selectorConfig.videoPlayerEngine.onStarPlayer(videoPlayer, media)
            }
        }
    }

    override fun setScaleDisplaySize(media: LocalMedia) {
        super.setScaleDisplaySize(media)
        if (!selectorConfig.isPreviewZoomEffect && screenWidth < screenHeight) {
            val layoutParams = videoPlayer!!.layoutParams
            if (layoutParams is FrameLayout.LayoutParams) {
                val playerLayoutParams = layoutParams
                playerLayoutParams.width = screenWidth
                playerLayoutParams.height = screenAppInHeight
                playerLayoutParams.gravity = Gravity.CENTER
            } else if (layoutParams is RelativeLayout.LayoutParams) {
                val playerLayoutParams = layoutParams
                playerLayoutParams.width = screenWidth
                playerLayoutParams.height = screenAppInHeight
                playerLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
            } else if (layoutParams is LinearLayout.LayoutParams) {
                val playerLayoutParams = layoutParams
                playerLayoutParams.width = screenWidth
                playerLayoutParams.height = screenAppInHeight
                playerLayoutParams.gravity = Gravity.CENTER
            } else if (layoutParams is ConstraintLayout.LayoutParams) {
                val playerLayoutParams = layoutParams
                playerLayoutParams.width = screenWidth
                playerLayoutParams.height = screenAppInHeight
                playerLayoutParams.topToTop = ConstraintSet.PARENT_ID
                playerLayoutParams.bottomToBottom = ConstraintSet.PARENT_ID
            }
        }
    }

    private fun playerDefaultUI() {
        isPlayed = false
        ivPlayButton.visibility = View.VISIBLE
        progress.visibility = View.GONE
        coverImageView.visibility = View.VISIBLE
        videoPlayer!!.visibility = View.GONE
        if (mPreviewEventListener != null) {
            mPreviewEventListener!!.onPreviewVideoTitle(null)
        }
    }

    private fun playerIngUI() {
        progress.visibility = View.GONE
        ivPlayButton.visibility = View.GONE
        coverImageView.visibility = View.GONE
        videoPlayer!!.visibility = View.VISIBLE
    }

    override fun onViewAttachedToWindow() {
        if (selectorConfig.videoPlayerEngine != null) {
            selectorConfig.videoPlayerEngine.onPlayerAttachedToWindow(videoPlayer)
            selectorConfig.videoPlayerEngine.addPlayListener(mPlayerListener)
        }
    }

    override fun onViewDetachedFromWindow() {
        if (selectorConfig.videoPlayerEngine != null) {
            selectorConfig.videoPlayerEngine.onPlayerDetachedFromWindow(videoPlayer)
            selectorConfig.videoPlayerEngine.removePlayListener(mPlayerListener)
        }
        playerDefaultUI()
    }

    /**
     * resume and pause play
     */
    override fun resumePausePlay() {
        if (isPlaying) {
            onPause()
        } else {
            onResume()
        }
    }

    override fun release() {
        if (selectorConfig.videoPlayerEngine != null) {
            selectorConfig.videoPlayerEngine.removePlayListener(mPlayerListener)
            selectorConfig.videoPlayerEngine.destroy(videoPlayer)
        }
    }

    init {
        ivPlayButton = itemView.findViewById(R.id.iv_play_video)
        progress = itemView.findViewById(R.id.progress)
        ivPlayButton.visibility =
            if (selectorConfig.isPreviewZoomEffect) View.GONE else View.VISIBLE
        if (selectorConfig.videoPlayerEngine == null) {
            selectorConfig.videoPlayerEngine = MediaPlayerEngine()
        }
        videoPlayer = selectorConfig.videoPlayerEngine.onCreateVideoPlayer(itemView.context)
        if (videoPlayer == null) {
            throw NullPointerException("onCreateVideoPlayer cannot be empty,Please implement " + VideoPlayerEngine::class.java)
        }
        if (videoPlayer!!.layoutParams == null) {
            videoPlayer!!.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        val viewGroup = itemView as ViewGroup
        if (viewGroup.indexOfChild(videoPlayer) != -1) {
            viewGroup.removeView(videoPlayer)
        }
        viewGroup.addView(videoPlayer, 0)
        videoPlayer!!.visibility = View.GONE
    }
}