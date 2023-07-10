package xy.xy.base.utils.encrypt

import android.util.Base64
import xy.xy.base.utils.Logger.d
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.SecretKeySpec

fun String.desDecode( des_password: String?): String {
    val data = Base64.decode(this, 0)
    if (des_password != null && des_password.length == 8 && data != null && data.isNotEmpty()) {
        try {
            val cipher = Cipher.getInstance("DES")
            val key = SecretKeySpec(des_password.toByteArray(), "DES")
            cipher.init(2, key)
            return String(cipher.doFinal(data))
        } catch (var5: NoSuchAlgorithmException) {
            var5.printStackTrace()
        } catch (var6: NoSuchPaddingException) {
            var6.printStackTrace()
        } catch (var7: InvalidKeyException) {
            var7.printStackTrace()
        } catch (var8: BadPaddingException) {
            var8.printStackTrace()
        } catch (var9: IllegalBlockSizeException) {
            var9.printStackTrace()
        }
    }
    return ""
}

fun String.desEncode(password: String): String {
    var encodeContent = ""
    try {
        val cipher = Cipher.getInstance("DES")
        val pdBytes = password.toByteArray()
        if (pdBytes != null && pdBytes.size == 8) {
            val key = SecretKeySpec(pdBytes, "DES")
            cipher.init(1, key)
            val bytes = toByteArray()
            if (bytes != null && bytes.isNotEmpty()) {
                val desEncodeResult = cipher.doFinal(bytes)
                val encode = Base64.encode(desEncodeResult, 0)
                encodeContent = String(encode)
            } else {
                encodeContent = ""
                d("desEncode: 请输入要加密内容")
            }
        } else {
            encodeContent = ""
            d("des", "desEncode: 请输入8位密码")
        }
    } catch (var9: NoSuchAlgorithmException) {
        var9.printStackTrace()
    } catch (var10: NoSuchPaddingException) {
        var10.printStackTrace()
    } catch (var11: InvalidKeyException) {
        var11.printStackTrace()
    } catch (var12: BadPaddingException) {
        var12.printStackTrace()
    } catch (var13: IllegalBlockSizeException) {
        var13.printStackTrace()
    }
    return encodeContent
}