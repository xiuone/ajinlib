package com.xy.base.web.agent

import android.content.Context
import android.webkit.WebSettings
import com.xy.base.utils.exp.getSpString

object UserAgentObject {
    private val SAVE_URL_AGENT = "SAVE_URL_AGENT"

    private fun getDefaultUserAgent(context: Context): String {
        var userAgent = WebSettings.getDefaultUserAgent(context)
        val sb = StringBuffer()
        var i = 0
        val length = userAgent.length
        while (i < length) {
            val c = userAgent[i]
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", c.code))
            } else {
                sb.append(c)
            }
            i++
        }
        return sb.toString()
    }

    fun getUserAgentList(context: Context): List<UserAgentMode>{
        val dataList = ArrayList<UserAgentMode>()
        dataList.add(
            UserAgentMode(
                context,
                "MR",
                getDefaultUserAgent(context)
            )
        )
        dataList.add(
            UserAgentMode(
                context,
                "Moto G4",
                "Mozilla/5.0 (Linux; Android 6.0.1; Moto G (4)) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Mobile Safari/537.36"
            )
        )
        dataList.add(
            UserAgentMode(
                context,
                "Galaxy S5",
                "Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Mobile Safari/537.36"
            )
        )
        dataList.add(
            UserAgentMode(
                context,
                "Pixel 2",
                "Mozilla/5.0 (Linux; Android 8.0; Pixel 2 Build/OPD3.170816.012) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Mobile Safari/537.36"
            )
        )
        dataList.add(
            UserAgentMode(
                context,
                "Pixel 2 XL",
                "Mozilla/5.0 (Linux; Android 8.0.0; Pixel 2 XL Build/OPD1.170816.004) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Mobile Safari/537.36"
            )
        )
        dataList.add(
            UserAgentMode(
                context,
                "IPhone 5/SE",
                "Mozilla/5.0 (iPhone; CPU iPhone OS 10_3_1 like Mac OS X) AppleWebKit/603.1.30 (KHTML, like Gecko) Version/10.0 Mobile/14E304 Safari/602.1"
            )
        )
        dataList.add(
            UserAgentMode(
                context,
                "IPhone 6/7/8",
                "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1"
            )
        )
        dataList.add(
            UserAgentMode(
                context,
                "IPhone 6/7/8 Plus",
                "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1"
            )
        )
        dataList.add(
            UserAgentMode(
                context,
                "IPhone X",
                "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1"
            )
        )
        dataList.add(
            UserAgentMode(
                context,
                "iPad",
                "Mozilla/5.0 (iPad; CPU OS 11_0 like Mac OS X) AppleWebKit/604.1.34 (KHTML, like Gecko) Version/11.0 Mobile/15A5341f Safari/604.1"
            )
        )
        dataList.add(
            UserAgentMode(
                context,
                "iPad Pro",
                "Mozilla/5.0 (iPad; CPU OS 11_0 like Mac OS X) AppleWebKit/604.1.34 (KHTML, like Gecko) Version/11.0 Mobile/15A5341f Safari/604.1"
            )
        )
        dataList.add(
            UserAgentMode(
                context,
                "Safari",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_4) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.1 Safari/605.1.15"
            )
        )
        dataList.add(
            UserAgentMode(
                context,
                "Chrome Mac",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Safari/537.36"
            )
        )
        dataList.add(
            UserAgentMode(
                context,
                "Chrome Windows",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Safari/537.36"
            )
        )
        dataList.add(
            UserAgentMode(
                context,
                "Edge Mac",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Safari/537.36 Edg/83.0.478.45"
            )
        )
        dataList.add(
            UserAgentMode(
                context,
                "Edge Windows",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Safari/537.36 Edg/83.0.478.45"
            )
        )
        dataList.add(
            UserAgentMode(
                context,
                "Firefox Mac",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:58.0) Gecko/20100101 Firefox/58.0"
            )
        )
        dataList.add(
            UserAgentMode(
                context,
                "Firefox Windows",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:76.0) Gecko/20100101 Firefox/76.0"
            )
        )
        dataList.add(
            UserAgentMode(
                context,
                "IE 9.0",
                "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0"
            )
        )
        dataList.add(
            UserAgentMode(
                context,
                "IE 8.0",
                "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0)"
            )
        )
        return dataList
    }


    fun getCurrentUserAgent(context: Context): String{
        return context.getSpString(SAVE_URL_AGENT, getDefaultUserAgent(context))?: getDefaultUserAgent(context)
    }
}