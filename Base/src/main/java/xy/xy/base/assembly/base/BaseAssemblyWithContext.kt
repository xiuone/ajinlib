package xy.xy.base.assembly.base

import xy.xy.base.dialog.impl.BottomDialogImpl
import xy.xy.base.dialog.impl.CenterDialogImpl
import xy.xy.base.dialog.listener.DialogImplListener

abstract class BaseAssemblyWithContext<T : BaseAssemblyViewWithContext>(view: T? = null) : BaseAssemblyBase<T>(view) {

    protected fun createBottomDialog(listener:DialogImplListener?): BottomDialogImpl?{
        val context = getContext()
        if (context == null || listener == null)
            return null
        return BottomDialogImpl(context, listener)
    }

    protected fun createCenterDialog(listener: DialogImplListener?): CenterDialogImpl?{
        val context = getContext()
        if (context == null || listener == null)
            return null
        return CenterDialogImpl(context, listener)
    }

    fun getContext() = view?.getPageContext()

    fun getCurrentAct() = view?.getCurrentAct()
    fun getCurrentFragment() = view?.getCurrentFragment()
}