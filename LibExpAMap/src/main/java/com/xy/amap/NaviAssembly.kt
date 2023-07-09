package com.xy.amap

import android.content.Intent
import android.net.Uri
import android.view.View
import com.amap.api.maps2d.model.LatLng
import com.xy.base.assembly.base.BaseAssemblyWithContext
import com.xy.base.assembly.base.BaseAssemblyViewWithContext
import com.xy.base.dialog.base.BaseDialog
import com.xy.base.dialog.listener.DialogCancelSureView
import com.xy.base.dialog.listener.DialogImplListener
import com.xy.base.utils.exp.*
import java.math.BigDecimal
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


/**
 * 去导航的界面
 */
class NaviAssembly(view: NaviAssemblyView) :BaseAssemblyWithContext<NaviAssembly.NaviAssemblyView>(view) ,DialogImplListener{
    private val x_pi = 3.14159265358979324 * 3000.0 / 180.0
    private var gdButton :View? = null
    private var baiduButton:View?= null
    private var tencentButton:View? = null

    private val gdKey = "androidamap://route?sourceApplication=amap&dlat=%s&dlon=%s&dname=%s&dev=0&t=1"
    private val tencentKey = "qqmap://map/marker?referer=&marker=coord:%s,%s;title:%s;addr:%s"
    private val baiduKey = "baidumap://map/marker?location=%s,%s&title=%s&content=%s&traffic=on&src=${getContext()?.packageName}"


    private val naviDialog by lazy { createBottomDialog(this) }

    override fun onCreateInit() {
        super.onCreateInit()
        addNavi(view?.onNaviView(),view?.onTagPosition())
    }

    private fun addNavi(view: View?,latLng: LatLng?){
        view?.setOnClick{
            naviDialog?.show(latLng)
        }
    }

    override fun dialogLayoutRes(): Int? = view?.dialogLayoutRes()

    override fun dialogInitView(dialog: BaseDialog) {
        super.dialogInitView(dialog)
        gdButton = view?.onGDView(dialog)
        baiduButton = view?.onBaiduView(dialog)
        tencentButton = view?.onTencentView(dialog)
        view?.onCreateDialogCancelView(dialog)?.setOnClick{
            dialog.dismiss()
        }
    }

    override fun dialogShow(dialog: BaseDialog, any: Any?) {
        super.dialogShow(dialog, any)
        if (any is LatLng){
            gdButton?.setOnClick{
                dialog.dismiss()
                goGDMap(any)
            }
            baiduButton?.setOnClick{
                dialog.dismiss()
                goBaidu(any)
            }
            tencentButton?.setOnClick{
                dialog.dismiss()
                goTencentMap(any)
            }
        }
    }


    private fun dataDigit(digit: Int, `in`: Double): Double = BigDecimal(`in`).setScale(digit, BigDecimal.ROUND_HALF_UP).toDouble()

    /**
     * 高德转百度地图
     */
    private fun gDToBD(gd_lat: Double, gd_lon: Double): DoubleArray {
        val bdLatLon = DoubleArray(2)
        val z: Double = sqrt(gd_lon * gd_lon + gd_lat * gd_lat) + 0.00002 * sin(gd_lat * x_pi)
        val theta: Double = atan2(gd_lat, gd_lon) + 0.000003 * cos(gd_lon * x_pi)
        bdLatLon[0] = dataDigit(6, z * sin(theta) + 0.006)
        bdLatLon[1] = dataDigit(6, z * cos(theta) + 0.0065)
        return bdLatLon
    }

    /**
     * 去高德地图
     */
    private fun goGDMap(latLng: LatLng) {
        if (getContext()?.isInstall("com.autonavi.minimap") == true) {
            val intent = Intent()
            intent.data = Uri.parse(String.format(gdKey,latLng.latitude,latLng.longitude,""))
            intent.action = Intent.ACTION_VIEW
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            getContext()?.startAppActivity(intent)
        } else {
            getContext()?.showToast(getContext()?.getResString(R.string.gd_un_install))
            getContext()?.startMark(getContext()?.packageName)
        }
    }


    /**百度地图 */
    private fun goBaidu(latLng: LatLng) {
        val bdLatLon = gDToBD(latLng.latitude, latLng.longitude)
        var latitude = bdLatLon[0]
        var longitude = bdLatLon[1]
        if (getContext()?.isInstall("com.baidu.BaiduMap") == true) { //传入指定应用包名
            val intent = Intent()
            intent.data = Uri.parse(String.format(baiduKey,latitude,longitude,"",""))
            intent.action = Intent.ACTION_VIEW
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            getContext().startAppActivity(intent)
        } else { //未安装
            getContext()?.showToast(getContext()?.getResString(R.string.baidu_un_install))
            getContext().startMark("com.baidu.BaiduMap")
        }
    }

    /**腾讯地图 */
    private fun goTencentMap(latLng: LatLng) {
        var latitude = 0.0
        var longitude = 0.0
        if (getContext()?.isInstall( "com.tencent.map") == true) { //传入指定应用包名
            val uri = Uri.parse(String.format(tencentKey,latitude,longitude,"",""))
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.action = Intent.ACTION_VIEW
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            getContext()?.startAppActivity(intent)
        } else {
            getContext()?.showToast(getContext()?.getResString(R.string.tencent_un_install))
            getContext()?.startMark("com.tencent.map")
        }
    }

    interface NaviAssemblyView : BaseAssemblyViewWithContext, DialogImplListener, DialogCancelSureView {
        fun onGDView(dialog: BaseDialog):View?
        fun onTencentView(dialog: BaseDialog):View?
        fun onBaiduView(dialog: BaseDialog):View
        fun onNaviView():View?
        fun onTagPosition():LatLng?
    }
}