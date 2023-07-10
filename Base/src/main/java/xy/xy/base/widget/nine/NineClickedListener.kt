package xy.xy.base.widget.nine

interface NineClickedListener<T: NineListener> {
    fun onNineClicked(data:MutableList<T>,item:T,postion:Int)
}