package xy.xy.base.utils.glide

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.annotation.NonNull
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okio.*
import java.io.IOException
import java.io.InputStream

@GlideModule
class GlideModuleProgress : AppGlideModule() {

    private val okHttpClient by lazy { createOkHttpClient() }

    private fun createOkHttpClient():OkHttpClient{
        return OkHttpClient.Builder().addInterceptor { chain ->
            val builder = chain.request().newBuilder()
            builder.addHeader("Accept-Encoding", "identity") //强迫服务器不走压缩
            chain.proceed(builder.build())
        }.addNetworkInterceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)
            response.newBuilder()
                .body(ProgressResponseBody(request.url().toString(), response.body()!!))
                .build()
        }.build()
    }



    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
        registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(okHttpClient))
    }


    /**
     * @author by sunfusheng on 2017/6/14.
     */
    private class ProgressResponseBody  (private val url: String, private val responseBody: ResponseBody) : ResponseBody() {

        private val mainThreadHandler by lazy { Handler(Looper.getMainLooper()) }
        private val bufferedSource by lazy {source(responseBody.source()).buffer()  }

        override fun contentType(): MediaType? = responseBody.contentType()

        override fun contentLength(): Long = responseBody.contentLength()

        override fun source(): BufferedSource = bufferedSource

        private fun source(source: Source): Source {
            return object : ForwardingSource(source) {
                var totalBytesRead: Long = 0
                var lastTotalBytesRead: Long = 0

                @Throws(IOException::class)
                override fun read(@NonNull sink: Buffer, byteCount: Long): Long {
                    val bytesRead = super.read(sink, byteCount)
                    totalBytesRead += if (bytesRead == -1L) 0 else bytesRead
                    if (lastTotalBytesRead != totalBytesRead) {
                        lastTotalBytesRead = totalBytesRead
                        val progress = (lastTotalBytesRead * 100 /contentLength()).toInt()
                        mainThreadHandler.post {
                            val list = GlideProgressManger.getProgressListener(url)
                            for (item in list){
                                item.onProgress(url,progress,totalBytesRead,contentLength())
                            }
                        }
                    }
                    return bytesRead
                }
            }
        }
    }


}