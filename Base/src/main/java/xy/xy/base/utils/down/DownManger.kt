package xy.xy.base.utils.down

import android.os.Handler
import android.os.Looper
import xy.xy.base.utils.ContextHolder
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class DownManger {

    fun<T> startDown(item:T,url:String?,dirPath:String?, name:String?=null,listener: DownFileListener<T>){
        if (url == null||!url.startsWith("http")){
            listener.downError(item)
            return
        }
        val request = Request.Builder().url(url).build()
        val okHttpClient = OkHttpClient()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Handler(Looper.getMainLooper()).post {
                    listener.downError(item)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    startDown(item,request, response,dirPath,name,listener)
                }else{
                    Handler(Looper.getMainLooper()).post {
                        listener.downError(item)
                    }
                }
            }
        })
    }

    private fun <T> startDown(item: T?,request: Request,response: Response,dirPath:String?, name:String?=null,listener: DownFileListener<T>){
        val encodedPath = request.url().encodedPath()
        val urlEndIndex: Int = encodedPath.lastIndexOf("/") + 1
        var filename: String = encodedPath.substring(urlEndIndex, encodedPath.length)
        filename = if (!name.isNullOrEmpty()) name else filename
        val dirFile = File(dirPath?:ContextHolder.getContext()?.filesDir.toString())
        if (!dirFile.exists())
            dirFile.mkdir()
        val file = File(dirPath,filename)
        if (file.exists()) {
            file.delete()
        }

        var `is`: InputStream? = null
        val buf = ByteArray(2048)
        var fos: FileOutputStream? = null
        try {
            var len = 0
            var sum: Long = 0
            val total = response.body()?.contentLength()?:1
            `is` = response.body()?.byteStream()
            if (`is`==null){
                Handler(Looper.getMainLooper()).post {
                    listener.downError(item)
                }
                return
            }
            fos = FileOutputStream(file)
            while (`is`.read(buf).also { len = it } != -1) {
                sum += len.toLong()
                fos.write(buf, 0, len)
                val finalSum = sum
                Handler(Looper.getMainLooper()).post {
                    listener.progress(finalSum.toFloat()/ total.toFloat(),total,finalSum)
                }
            }
            fos.flush()
            Handler(Looper.getMainLooper()).post {
                if (file.exists())
                    listener.downSuc(file,item)
                else{
                }
            }
        }catch (e:Exception){
            Handler(Looper.getMainLooper()).post {
                listener.downError(item)
            }
        } finally {
            try {
                response.body()?.close()
                `is`?.close()
                fos?.close()
            } catch (e: IOException) {

            }
        }
    }


    interface DownFileListener<T> {
        fun start(item: T?){}
        fun progress(progress: Float,fileSize:Long,dowSize:Long){}
        fun downSuc(file: File, item:T?){}
        fun downError(item:T?){}
    }

    companion object{
        val instance by lazy { DownManger() }
    }
}