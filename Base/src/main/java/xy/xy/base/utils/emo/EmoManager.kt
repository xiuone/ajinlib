package xy.xy.base.utils.emo

import android.content.Context
import android.graphics.drawable.Drawable
import xy.xy.base.utils.exp.getResDrawable
import kotlin.collections.HashMap

class EmoManager {
    private val emoEntry = HashMap<String, EmoEntryMode>()
    private val emoList by lazy { ArrayList<EmoEntryMode>() }

    fun getDrawable(context: Context?, text: String?): Drawable?{
        if (text.isNullOrEmpty() || context == null)return null
        val emo = emoEntry[text]?:return null
        return context.getResDrawable(emo.res)
    }

    fun addEmoEntry(tag:String,res:Int):EmoManager{
        val item = EmoEntryMode(tag,res)
        emoEntry[tag] = item
        emoList.add(item)
        return this
    }

    fun getEmoEntry() = emoList

    data class EmoEntryMode (val text: String, val res: Int)

    companion object{
        val instance by lazy { EmoManager() }
    }
}
