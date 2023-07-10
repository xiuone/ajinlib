package xy.xy.base.widget.recycler.holder

import android.util.SparseArray
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView

open class BaseViewHolder(item:View) : RecyclerView.ViewHolder(item){
    protected val context by lazy { itemView.context }
    val views: SparseArray<View?> = SparseArray()
    fun <VI : View?> getView(@IdRes viewId: Int?): VI? {
        if (viewId == null)return  null
        var view = views[viewId]
        if (view == null) {
            view = itemView.findViewById(viewId)
        }
        return if (view != null) {
            views.put(viewId, view)
            view as VI
        } else null
    }

    fun getTextView(@IdRes viewId: Int?): TextView? {
        return getView<TextView>(viewId)
    }

    fun getImageView(@IdRes viewId: Int?): ImageView? {
        return getView<ImageView>(viewId)
    }

    fun setText(@IdRes viewId: Int?, string: String?) {
        val textView = getTextView(viewId) ?: return
        textView.text = string
    }

}