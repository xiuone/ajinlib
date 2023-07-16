package xy.xy.base.utils.exp

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions

fun ImageView.loadImageWithCenter(any: Any?,  vararg transformations: Transformation<Bitmap>){
    loadImage(any,CenterCrop(), *transformations)
}

fun ImageView.loadImageWithCenter(any: Any?,res:Int?,  vararg transformations: Transformation<Bitmap>){
    if (res == null)
        loadImage(any,CenterCrop(), *transformations)
    else
        loadImage(any,res,CenterCrop(), *transformations)
}

fun ImageView.loadImageWithCenter(any: Any?,requestListener:RequestListener<Drawable>,  vararg transformations: Transformation<Bitmap>){
    loadImage(any,requestListener,CenterCrop(), *transformations)
}


fun ImageView.loadImageWithCenter(any: Any?,res:Int,requestListener:RequestListener<Drawable>,  vararg transformations: Transformation<Bitmap>){
    loadImage(any,res,requestListener,CenterCrop(), *transformations)
}




fun ImageView.loadImage(any: Any?, vararg transformations: Transformation<Bitmap>, ){
    if (any == null)return
    Glide.with(context.applicationContext)
        .load(any)
        .apply(getRequestOptions(*transformations))
        .into(this)
}

fun ImageView.loadImage(any: Any?,res:Int?, vararg transformations: Transformation<Bitmap>, ){
    if (any == null && res != null){
        setImageResource(res)
        return
    }
    if (res == null){
        Glide.with(context.applicationContext).load(any).apply(getRequestOptions(*transformations)).into(this)
    }else {
        Glide.with(context.applicationContext).load(any).apply(getRequestOptions(res, *transformations)).into(this)
    }
}

fun ImageView.loadImage(any: Any?,requestListener:RequestListener<Drawable>, vararg transformations: Transformation<Bitmap>, ){
    if (any == null)return
    Glide.with(context.applicationContext).load(any).apply(getRequestOptions(*transformations)).listener(requestListener).into(this)
}

fun ImageView.loadImage(any: Any?,res:Int,requestListener:RequestListener<Drawable>, vararg transformations: Transformation<Bitmap>, ){
    if (any == null){
        setImageResource(res)
        return
    }
    Glide.with(context.applicationContext).load(any).apply(getRequestOptions(res,*transformations)).listener(requestListener).into(this)
}




fun getRequestOptions(vararg transformations: Transformation<Bitmap>): RequestOptions {
    if (transformations.isEmpty())
        return RequestOptions()
    val transformation = MultiTransformation(*transformations)
    return RequestOptions.bitmapTransform(transformation)
}

fun getRequestOptions(res:Int,vararg transformations: Transformation<Bitmap>): RequestOptions {
    if (transformations.isEmpty())
        return RequestOptions().placeholder(res).error(res)
    val transformation = MultiTransformation(*transformations)
    return RequestOptions.bitmapTransform(transformation).placeholder(res).error(res)
}
