package com.luck.picture.lib.engine

import android.content.Context
import android.view.View
import com.luck.picture.lib.config.PictureMimeType.isHasHttp
import com.luck.picture.lib.config.SelectorProviders.Companion.instance
import com.luck.picture.lib.config.SelectorProviders.selectorConfig
import com.luck.picture.lib.config.SelectorConfig
import com.luck.picture.lib.config.SelectorProviders
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnPlayerListener
import com.luck.picture.lib.widget.MediaPlayerView
import java.util.concurrent.CopyOnWriteArrayList

/**
 * @author：luck
 * @date：2022/7/1 22:09 下午
 * @describe：MediaPlayerEngine
 */
class MediaPlayerEngine : VideoPlayerEngine<MediaPlayerView> {
    /**
     * 播放状态监听器集
     */
    private val listeners = CopyOnWriteArrayList<OnPlayerListener>()
    override fun onCreateVideoPlayer(context: Context?): View {
        return MediaPlayerView(context!!)
    }

    override fun onStarPlayer(player: MediaPlayerView, media: LocalMedia) {
        val availablePath = media.availablePath
        val mediaPlayer = player.mediaPlayer
        val surfaceView = player.surfaceView
        surfaceView.setZOrderOnTop(isHasHttp(availablePath))
        val config = instance!!.selectorConfig
        mediaPlayer.isLooping = config.isLoopAutoPlay
        player.start(availablePath)
    }

    override fun onResume(player: MediaPlayerView) {
        val mediaPlayer = player.mediaPlayer
        mediaPlayer?.start()
    }

    override fun onPause(player: MediaPlayerView) {
        val mediaPlayer = player.mediaPlayer
        mediaPlayer?.pause()
    }

    override fun isPlaying(player: MediaPlayerView): Boolean {
        val mediaPlayer = player.mediaPlayer
        return mediaPlayer != null && mediaPlayer.isPlaying
    }

    override fun addPlayListener(playerListener: OnPlayerListener) {
        if (!listeners.contains(playerListener)) {
            listeners.add(playerListener)
        }
    }

    override fun removePlayListener(playerListener: OnPlayerListener?) {
        if (playerListener != null) {
            listeners.remove(playerListener)
        } else {
            listeners.clear()
        }
    }

    override fun onPlayerAttachedToWindow(player: MediaPlayerView) {
        val mediaPlayer = player.initMediaPlayer()
        mediaPlayer.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.start()
            for (i in listeners.indices) {
                val playerListener = listeners[i]
                playerListener.onPlayerReady()
            }
        }
        mediaPlayer.setOnCompletionListener { mediaPlayer ->
            mediaPlayer.reset()
            for (i in listeners.indices) {
                val playerListener = listeners[i]
                playerListener.onPlayerEnd()
            }
            player.clearCanvas()
        }
        mediaPlayer.setOnErrorListener { mp, what, extra ->
            for (i in listeners.indices) {
                val playerListener = listeners[i]
                playerListener.onPlayerError()
            }
            false
        }
    }

    override fun onPlayerDetachedFromWindow(player: MediaPlayerView) {
        player.release()
    }

    override fun destroy(player: MediaPlayerView) {
        player.release()
    }
}