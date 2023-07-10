package xy.xy.base.widget.nine

import java.io.Serializable

interface NineListener :Serializable{
    fun onWidth():Int
    fun onHeight():Int
    fun onThumb():String?
    fun isVideo():Boolean
}