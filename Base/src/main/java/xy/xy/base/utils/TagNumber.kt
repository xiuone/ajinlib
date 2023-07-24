package xy.xy.base.utils

object TagNumber {
    private val tagNumber by lazy { HashMap<String?,Long>() }

    fun getTag(name:String?):String{
        val value = (tagNumber[name]?:0)+1
        tagNumber[name] = value
        return "$name----$value"
    }
}