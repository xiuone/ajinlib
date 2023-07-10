package xy.xy.base.db.media

import xy.xy.base.BuildConfig
import xy.xy.base.db.base.DaoHelp
import xy.xy.base.utils.ContextHolder
import xy.xy.base.utils.Logger

class MediaManger : MediaNotify(){

    private class MediaDao : DaoHelp<MediaMode>("MediaMode", BuildConfig.DB_VERSION, ContextHolder.getContext(), MediaMode::class.java)

    private val userDao by lazy { MediaDao() }

    fun getAllList():MutableList<xy.xy.base.db.media.MediaMode> {
        try {
            return userDao.queryBuilder.query()?:ArrayList()
        }catch (e:Exception){
            Logger.e("getAllUserList====e:${e.message}")
        }
        return ArrayList()
    }

    fun getMedia(id:Long): xy.xy.base.db.media.MediaMode?{
        try {
            val data = userDao.queryBuilder?.where()?.eq("id",id)?.query()?:ArrayList()
            return if (data.isEmpty()) null else data[0]
        }catch (e:Exception){
            Logger.e("getAllUserList====e:${e.message}")
        }
        return null
    }


    fun saveMedia(mediaMode: xy.xy.base.db.media.MediaMode?){
        if (mediaMode == null)return
        val oldMedia = getMedia(mediaMode.id)
        if (oldMedia != null) {
            if (oldMedia.isCompleteSame(mediaMode))return
            mediaMode._id = oldMedia._id
        }
        userDao.save(mediaMode)
        onMediaChange(mediaMode)
    }


    companion object{
        val instance: MediaManger by lazy {  MediaManger() }
    }
}