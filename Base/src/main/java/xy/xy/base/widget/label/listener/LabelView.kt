package xy.xy.base.widget.label.listener

interface LabelView<T> {
    fun setData(data:MutableList<T>)
    fun getData():MutableList<T>
    fun setVisibility(visibility: Int)
    fun setOnViewListener(listener: LabelListener<T>)
    fun setOnClickedListener(listener: LabelViewClickedListener<T>)
}