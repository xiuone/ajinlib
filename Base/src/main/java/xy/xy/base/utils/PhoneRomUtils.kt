package xy.xy.base.utils

import android.os.Build
import android.os.Environment
import android.text.TextUtils
import java.io.*
import java.lang.reflect.InvocationTargetException
import java.util.*

/**
 * author : Android 轮子哥
 * github : https://github.com/getActivity/XXPermissions
 * time   : 2023/04/05
 * desc   : 厂商 Rom 工具类
 */
object PhoneRomUtils {
    private val ROM_HUAWEI = arrayOf("huawei")
    private val ROM_VIVO = arrayOf("vivo")
    private val ROM_XIAOMI = arrayOf("xiaomi")
    private val ROM_OPPO = arrayOf("oppo")
    private val ROM_LEECO = arrayOf("leeco", "letv")
    private val ROM_360 = arrayOf("360", "qiku")
    private val ROM_ZTE = arrayOf("zte")
    private val ROM_ONEPLUS = arrayOf("oneplus")
    private val ROM_NUBIA = arrayOf("nubia")
    private val ROM_SAMSUNG = arrayOf("samsung")
    private const val ROM_NAME_MIUI = "ro.miui.ui.version.name"
    private const val VERSION_PROPERTY_HUAWEI = "ro.build.version.emui"
    private const val VERSION_PROPERTY_VIVO = "ro.vivo.os.build.display.id"
    private const val VERSION_PROPERTY_XIAOMI = "ro.build.version.incremental"
    private val VERSION_PROPERTY_OPPO =
        arrayOf("ro.build.version.opporom", "ro.build.version.oplusrom.display")
    private const val VERSION_PROPERTY_LEECO = "ro.letv.release.version"
    private const val VERSION_PROPERTY_360 = "ro.build.uiversion"
    private const val VERSION_PROPERTY_ZTE = "ro.build.MiFavor_version"
    private const val VERSION_PROPERTY_ONEPLUS = "ro.rom.version"
    private const val VERSION_PROPERTY_NUBIA = "ro.build.rom.id"

    /**
     * 判断当前厂商系统是否为 emui
     */
    fun isEmui() = !TextUtils.isEmpty(getPropertyName(VERSION_PROPERTY_HUAWEI))

    /**
     * 判断当前厂商系统是否为 miui
     */
    fun isMiui() = !TextUtils.isEmpty(getPropertyName(ROM_NAME_MIUI))

    /**
     * 判断当前厂商系统是否为 ColorOs
     */
    fun isColorOs(): Boolean{
        for (property in VERSION_PROPERTY_OPPO) {
            val versionName = getPropertyName(property)
            if (TextUtils.isEmpty(versionName)) {
                continue
            }
            return true
        }
        return false
    }

    /**
     * 判断当前厂商系统是否为 OriginOS
     */
    fun isOriginOs() = !TextUtils.isEmpty(getPropertyName(VERSION_PROPERTY_VIVO))// 暂时无法通过下面的方式判断是否为 OneUI，只能通过品牌和机型来判断

    /**
     * 判断当前厂商系统是否为 OneUI
     */
    fun isOneUi(): Boolean = isRightRom(brand(), manufacturer(), *ROM_SAMSUNG)

    /**
     * 判断当前是否为鸿蒙系统
     */
    fun isHarmonyOs(): Boolean{
        return try {
            val buildExClass = Class.forName("com.huawei.system.BuildEx")
            val osBrand = buildExClass.getMethod("getOsBrand").invoke(buildExClass)
            "Harmony".equals(osBrand.toString(), ignoreCase = true)
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            false
        }
    }

    /**
     * 判断 miui 优化开关（默认开启，关闭步骤为：开发者选项-> 启动 MIUI 优化 -> 点击关闭）
     * 需要注意的是，关闭 miui 优化后，可以跳转到小米定制的权限请求页面，但是开启权限仍然是没有效果的
     * 另外关于 miui 国际版开发者选项中是没有 miui 优化选项的，但是代码判断是有开启 miui 优化，也就是默认开启，这样是正确的
     * 相关 Github issue 地址：https://github.com/getActivity/XXPermissions/issues/38
     */
    fun isMiuiOptimization(): Boolean {
            try {
                val clazz = Class.forName("android.os.SystemProperties")
                val getMethod = clazz.getMethod("get",
                    String::class.java,
                    String::class.java)
                val ctsValue = getMethod.invoke(clazz, "ro.miui.cts", "").toString()
                val getBooleanMethod = clazz.getMethod("getBoolean",
                    String::class.java,
                    Boolean::class.javaPrimitiveType)
                return java.lang.Boolean.parseBoolean(getBooleanMethod.invoke(clazz, "persist.sys.miui_optimization", "1" != ctsValue).toString())
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
            return true
        }// 需要注意的是 vivo iQOO 9 Pro Android 12 获取到的厂商版本号是 OriginOS Ocean// 需要注意的是 华为畅享 5S Android 5.1 获取到的厂商版本号是 EmotionUI 3，而不是 3.1 或者 3.0 这种

    /**
     * 返回厂商系统版本号
     */
    fun romVersionName():String{
            val brand = brand()
            val manufacturer = manufacturer()
            if (isRightRom(brand, manufacturer, *ROM_HUAWEI)) {
                val version = getPropertyName(VERSION_PROPERTY_HUAWEI)
                val temp = version.split("_").toTypedArray()
                return if (temp.size > 1) {
                    temp[1]
                } else {
                    // 需要注意的是 华为畅享 5S Android 5.1 获取到的厂商版本号是 EmotionUI 3，而不是 3.1 或者 3.0 这种
                    if (version.contains("EmotionUI")) {
                        version.replaceFirst("EmotionUI\\s*".toRegex(), "")
                    } else version
                }
            }
            if (isRightRom(brand, manufacturer, *ROM_VIVO)) {
                // 需要注意的是 vivo iQOO 9 Pro Android 12 获取到的厂商版本号是 OriginOS Ocean
                return getPropertyName(VERSION_PROPERTY_VIVO)
            }
            if (isRightRom(brand, manufacturer, *ROM_XIAOMI)) {
                return getPropertyName(VERSION_PROPERTY_XIAOMI)
            }
            if (isRightRom(brand, manufacturer, *ROM_OPPO)) {
                for (property in VERSION_PROPERTY_OPPO) {
                    val versionName = getPropertyName(property)
                    if (TextUtils.isEmpty(property)) {
                        continue
                    }
                    return versionName
                }
                return ""
            }
            if (isRightRom(brand, manufacturer, *ROM_LEECO)) {
                return getPropertyName(VERSION_PROPERTY_LEECO)
            }
            if (isRightRom(brand, manufacturer, *ROM_360)) {
                return getPropertyName(VERSION_PROPERTY_360)
            }
            if (isRightRom(brand, manufacturer, *ROM_ZTE)) {
                return getPropertyName(VERSION_PROPERTY_ZTE)
            }
            if (isRightRom(brand, manufacturer, *ROM_ONEPLUS)) {
                return getPropertyName(VERSION_PROPERTY_ONEPLUS)
            }
            return if (isRightRom(brand, manufacturer, *ROM_NUBIA)) {
                getPropertyName(VERSION_PROPERTY_NUBIA)
            } else getPropertyName("")
        }

    private fun isRightRom(brand: String, manufacturer: String, vararg names: String): Boolean {
        for (name in names) {
            if (brand.contains(name) || manufacturer.contains(name)) {
                return true
            }
        }
        return false
    }

    private fun brand() =  Build.BRAND.lowercase(Locale.getDefault())

    private fun manufacturer() = Build.MANUFACTURER.lowercase(Locale.getDefault())

    private fun getPropertyName(propertyName: String): String {
        var result = ""
        if (!TextUtils.isEmpty(propertyName)) {
            result = getSystemProperty(propertyName)
        }
        return result
    }

    private fun getSystemProperty(name: String): String {
        var prop = getSystemPropertyByShell(name)
        if (!TextUtils.isEmpty(prop)) {
            return prop
        }
        prop = getSystemPropertyByStream(name)
        if (!TextUtils.isEmpty(prop)) {
            return prop
        }
        return if (Build.VERSION.SDK_INT < 28) {
            getSystemPropertyByReflect(name)
        } else prop
    }

    private fun getSystemPropertyByShell(propName: String): String {
        var input: BufferedReader? = null
        try {
            val p = Runtime.getRuntime().exec("getprop $propName")
            input = BufferedReader(InputStreamReader(p.inputStream), 1024)
            val ret = input.readLine()
            if (ret != null) {
                return ret
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (input != null) {
                try {
                    input.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return ""
    }

    private fun getSystemPropertyByStream(key: String): String {
        try {
            val prop = Properties()
            val `is` = FileInputStream(
                File(Environment.getRootDirectory(), "build.prop")
            )
            prop.load(`is`)
            return prop.getProperty(key, "")
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""
    }

    private fun getSystemPropertyByReflect(key: String): String {
        try {
            val clz = Class.forName("android.os.SystemProperties")
            val getMethod = clz.getMethod("get",
                String::class.java,
                String::class.java)
            return getMethod.invoke(clz, key, "") as String
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        return ""
    }
}