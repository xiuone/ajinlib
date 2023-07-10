package xy.xy.base.db.media

import xy.xy.base.utils.notify.NotifyBase

abstract class MediaNotify:NotifyBase<MediaNotify.MediaListener>() {

    fun onMediaChange(item: MediaMode) = findItem { it.onMediaChange(item) }

    interface MediaListener{
        fun onMediaChange(item: MediaMode)
    }
}