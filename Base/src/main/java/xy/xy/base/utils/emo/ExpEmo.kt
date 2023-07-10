package xy.xy.base.utils.emo

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.*
import android.text.style.ImageSpan
import android.widget.EditText
import android.widget.TextView
import xy.xy.base.listener.AppTextWatcher
import xy.xy.base.utils.exp.addAppTextChangedListener
import java.util.regex.Matcher
import java.util.regex.Pattern

private val SMALL_SCALE by lazy { 0.45f }
private val pattern by lazy { Pattern.compile("\\[[^\\[]{1,10}\\]") }

fun TextView?.replaceEmoticons( str: String?) {
    val context = this?.context?:return
    this.text = replaceEmoticons(context, str, SMALL_SCALE,  ImageSpan.ALIGN_BOTTOM)
}

fun EditText?.addEmoticons() {
    val context = this?.context?:return
    addAppTextChangedListener(object :AppTextWatcher{
        override fun afterTextChanged(editable: Editable?) {
            replaceEmoticons(context,editable,SMALL_SCALE,  ImageSpan.ALIGN_BOTTOM)
        }
    })
}

private fun replaceEmoticons(context: Context, editable: Editable?, scale: Float, align: Int) {
    val value = editable?.toString()?:return
    val matcher: Matcher = pattern.matcher(value)
    while (matcher.find()) {
        val start = matcher.start()
        val end = matcher.end()
        val emo = value.substring(start, end)
        val drawable = getEmoDrawable(context, emo, scale)
        if (drawable != null) {
            val span = ImageSpan(drawable, align)
            editable.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
}

private fun replaceEmoticons(context: Context, value: String?, scale: Float, align: Int): SpannableString {
    var value = value?:""
    if (TextUtils.isEmpty(value)) {
        value = ""
    }
    val mSpannableString = SpannableString(value)
    val matcher: Matcher = pattern.matcher(value)
    while (matcher.find()) {
        val start = matcher.start()
        val end = matcher.end()
        val emo = value.substring(start, end)
        val drawable = getEmoDrawable(context, emo, scale)
        if (drawable != null) {
            val span = ImageSpan(drawable, align)
            mSpannableString.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
    return mSpannableString
}


private fun getEmoDrawable(context: Context, text: String, scale: Float): Drawable? {
    val drawable = EmoManager.instance.getDrawable(context, text)
    if (drawable != null) {
        val width = (drawable.intrinsicWidth * scale).toInt()
        val height = (drawable.intrinsicHeight * scale).toInt()
        drawable.setBounds(0, 0, width, height)
    }
    return drawable
}
