package camerax.luck.lib.camerax.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import camerax.luck.lib.camerax.CustomCameraConfig
import camerax.luck.lib.camerax.CustomCameraType
import camerax.luck.lib.camerax.listener.CaptureListener
import camerax.luck.lib.camerax.listener.ClickListener
import camerax.luck.lib.camerax.listener.TypeListener
import xy.xy.base.R
import xy.xy.base.utils.exp.getScreenWidth

/**
 * @author：luck
 * @date：2019-01-04 13:41
 * @describe：CaptureLayout
 */
class CaptureLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {
    var captureListener: CaptureListener? = null//拍照按钮监听
    var typeListener: TypeListener? = null//拍照或录制后接结果按钮监听
    var leftClickListener: ClickListener? = null //左边按钮监听
    var rightClickListener: ClickListener? = null//右边按钮监听

    private val progressBar by lazy { ProgressBar(context) }// 拍照等待loading
    private val captureButton by lazy { CaptureButton(context, buttonSize) }//拍照按钮
    private val confirmButton by lazy { TypeButton(context, TypeButton.TYPE.TYPE_CONFIRM, buttonSize) }//确认按钮
    private val cancelButton by lazy {  TypeButton(context, TypeButton.TYPE.TYPE_CANCEL, buttonSize) }//取消按钮
    private val returnButton by lazy { ReturnButton(context, (buttonSize / 2.5f).toInt()) }//返回按钮
    private val customLeftIv by lazy { ImageView(context) }//左边自定义按钮
    private val customRightIv by lazy { ImageView(context) }//右边自定义按钮
    private val tipTv by lazy { TextView(context) }//提示文本

    private val isPortrait by lazy { this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT }
    private val screenWidth by lazy { context.getScreenWidth() }
    private val layoutWidth by lazy { if (isPortrait) screenWidth else screenWidth/2 }
    private val layoutHeight by lazy { buttonSize + buttonSize / 5 * 2 + 100 }
    private val buttonSize by lazy {  (layoutWidth / 4.5f).toInt() }
    private var iconLeft = 0
    private var iconRight = 0

    init {
        setWillNotDraw(false)
        initProgressBar()
        initCapture()
        initConfirm()
        initCancel()
        initReturn()
        initCustomLeftIv()
        initCustomRightIv()
        initTip()
        this.addView(captureButton)
        this.addView(progressBar)
        this.addView(cancelButton)
        this.addView(confirmButton)
        this.addView(returnButton)
        this.addView(customLeftIv)
        this.addView(customRightIv)
        this.addView(tipTv)
        initEvent()
    }

    //进度
    private fun initProgressBar(){
        val progressBarParam = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        progressBarParam.gravity = Gravity.CENTER
        progressBar.layoutParams = progressBarParam
        progressBar.visibility = GONE
    }

    //拍照按钮
    private fun initCapture(){
        val captureButtonParam = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        captureButtonParam.gravity = Gravity.CENTER
        captureButton.layoutParams = captureButtonParam
        captureButton.captureListener = object : CaptureListener {
            override fun takePictures() {
                captureListener?.takePictures()
                startAlphaAnimation()
            }

            override fun recordShort(time: Long) {
                captureListener?.recordShort(time)
            }

            override fun recordStart() {
                captureListener?.recordStart()
                startAlphaAnimation()
            }

            override fun recordEnd(time: Long) {
                captureListener?.recordEnd(time)
                startTypeBtnAnimator()
            }

            override fun changeTime(time: Long) {
                captureListener?.changeTime(time)
            }

            override fun recordZoom(zoom: Float) {
                captureListener?.recordZoom(zoom)
            }

            override fun recordError() {
                captureListener?.recordError()
            }
        }
    }

    //取消按钮
    private fun initCancel(){
        val cancelButtonParam = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        cancelButtonParam.gravity = Gravity.CENTER_VERTICAL
        cancelButtonParam.setMargins(layoutWidth / 4 - buttonSize / 2, 0, 0, 0)
        cancelButton.layoutParams = cancelButtonParam
        cancelButton.setOnClickListener {
            typeListener?.cancel()
        }
    }

    //确认按钮
    private fun initConfirm(){
        val confirmButtonParam = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        confirmButtonParam.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
        confirmButtonParam.setMargins(0, 0, layoutWidth / 4 - buttonSize / 2, 0)
        confirmButton.layoutParams = confirmButtonParam
        confirmButton.setOnClickListener {
            typeListener?.confirm()
        }
    }

    //返回按钮
    private fun initReturn(){
        val returnButtonParam = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        returnButtonParam.gravity = Gravity.CENTER_VERTICAL
        returnButtonParam.setMargins(layoutWidth / 6, 0, 0, 0)
        returnButton.layoutParams = returnButtonParam
        returnButton.setOnClickListener {
            leftClickListener?.onClick()
        }
    }

    //左边自定义按钮
    private fun initCustomLeftIv(){
        val customLeftParam =
            LayoutParams((buttonSize / 2.5f).toInt(), (buttonSize / 2.5f).toInt())
        customLeftParam.gravity = Gravity.CENTER_VERTICAL
        customLeftParam.setMargins(layoutWidth / 6, 0, 0, 0)
        customLeftIv.layoutParams = customLeftParam
        customLeftIv.setOnClickListener {
            leftClickListener?.onClick()
        }
    }
    //右边自定义按钮
    private fun initCustomRightIv(){
        val customRightParam =
            LayoutParams((buttonSize / 2.5f).toInt(), (buttonSize / 2.5f).toInt())
        customRightParam.gravity = Gravity.CENTER_VERTICAL or Gravity.RIGHT
        customRightParam.setMargins(0, 0, layoutWidth / 6, 0)
        customRightIv.layoutParams = customRightParam
        customRightIv.setOnClickListener {
            rightClickListener?.onClick()
        }
    }
    //提示
    private fun initTip(){
        val txtParam = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        txtParam.gravity = Gravity.CENTER_HORIZONTAL
        txtParam.setMargins(0, 0, 0, 0)
        tipTv.text = captureTip()
        tipTv.setTextColor(-0x1)
        tipTv.gravity = Gravity.CENTER
        tipTv.layoutParams = txtParam
    }
    //默认TypeButton为隐藏
    private fun initEvent() {
        customRightIv.visibility = GONE
        cancelButton.visibility = GONE
        confirmButton.visibility = GONE
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(layoutWidth, layoutHeight)
    }

    fun startTypeBtnAnimator() {
        //拍照录制结果后的动画
        if (iconLeft != 0) customLeftIv.visibility = GONE else returnButton.visibility = GONE
        if (iconRight != 0) customRightIv.visibility = GONE
        captureButton.visibility = GONE
        cancelButton.visibility = VISIBLE
        confirmButton.visibility = VISIBLE
        cancelButton.isClickable = false
        confirmButton.isClickable = false
        customLeftIv.visibility = GONE
        val cancelAnimator = ObjectAnimator.ofFloat(cancelButton, "translationX", (layoutWidth / 4).toFloat(), 0f)
        val confirmAnimator = ObjectAnimator.ofFloat(confirmButton, "translationX", (-layoutWidth / 4).toFloat(), 0f)
        val set = AnimatorSet()
        set.playTogether(cancelAnimator, confirmAnimator)
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                cancelButton.isClickable = true
                confirmButton.isClickable = true
            }
        })
        set.duration = 500
        set.start()
    }

    private fun captureTip(): String{
        return when (captureButton.buttonFeatures) {
            CustomCameraType.BUTTON_STATE_ONLY_CAPTURE -> context.getString(R.string.picture_photo_pictures)
            CustomCameraType.BUTTON_STATE_ONLY_RECORDER -> context.getString(R.string.picture_photo_recording)
            else -> context.getString(R.string.picture_photo_camera)
        }
    }

    fun setButtonCaptureEnabled(enabled: Boolean) {
        progressBar.visibility = if (enabled) GONE else VISIBLE
        captureButton.isTakeCamera = enabled
    }

    fun setCaptureLoadingColor(color: Int) {
        val colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(color, BlendModeCompat.SRC_IN)
        progressBar.indeterminateDrawable.colorFilter = colorFilter
    }

    fun resetCaptureLayout() {
        captureButton.resetState()
        cancelButton.visibility = GONE
        confirmButton.visibility = GONE
        captureButton.visibility = VISIBLE
        tipTv.text = captureTip()
        tipTv.visibility = VISIBLE
        if (iconLeft != 0) customLeftIv.visibility = VISIBLE else returnButton.visibility = VISIBLE
        if (iconRight != 0) customRightIv.visibility = VISIBLE
    }

    fun startAlphaAnimation() {
        tipTv.visibility = INVISIBLE
    }

    fun setTextWithAnimation(tip: String?) {
        tipTv.text = tip
        val tipAnimator = ObjectAnimator.ofFloat(tipTv, "alpha", 0f, 1f, 1f, 0f)
        tipAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                tipTv.setText(captureTip())
                tipTv.alpha = 1f
            }
        })
        tipAnimator.duration = 2500
        tipAnimator.start()
    }

    fun setDuration(duration: Int) {
        captureButton.maxDuration = duration
    }

    fun setMinDuration(duration: Int) {
        captureButton.minDuration = duration
    }
    fun setProgressColor(color: Int) {
        this.captureButton.progressColor = color
    }

    fun setButtonFeatures(state: CustomCameraType) {
        captureButton.buttonFeatures = state
        tipTv.text = captureTip()
    }

    fun setTip(tip: String?) {
        tipTv.text = tip
    }

    fun showTip() {
        tipTv.visibility = VISIBLE
    }

    fun setIconSrc(iconLeft: Int, iconRight: Int) {
        this.iconLeft = iconLeft
        this.iconRight = iconRight
        if (this.iconLeft != 0) {
            customLeftIv.setImageResource(iconLeft)
            customLeftIv.visibility = VISIBLE
            returnButton.visibility = GONE
        } else {
            customLeftIv.visibility = GONE
            returnButton.visibility = VISIBLE
        }
        if (this.iconRight != 0) {
            customRightIv.setImageResource(iconRight)
            customRightIv.visibility = VISIBLE
        } else {
            customRightIv.visibility = GONE
        }
    }
}