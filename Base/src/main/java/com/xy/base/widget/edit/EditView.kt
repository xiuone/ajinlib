package com.xy.base.widget.edit

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.xy.base.R
import com.xy.base.utils.exp.getResColor
import com.xy.base.utils.exp.getResDimension

class EditView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : RelativeLayout(context, attrs, defStyleAttr), View.OnFocusChangeListener,TextWatcher, TextView.OnEditorActionListener{
    private var mDefaultBackgroundColor = R.drawable.bg_transparent
    private var mFocusBackgroundColor = R.drawable.bg_transparent

    private var hintText :String ?= ""
    private var mTextSize = getContext().getResDimension(R.dimen.sp_11)
    private var mTextColor = getContext().getResColor(R.color.gray_3333)
    private var mTextHintColor = getContext().getResColor(R.color.gray_9999)

    private var leftIconRes = R.drawable.bg_transparent
    private var leftIconSize = 0;
    private var leftIconPadding = 0;

    private var delIconRes = R.drawable.bg_transparent
    private var delIconSize = 0
    private var delIconPadding = 0

    private val leftView by lazy { AppCompatImageView(context) }
    private val editView by lazy { EditText(context) }
    private val delView by lazy { AppCompatImageView(context) }

    var editListener :EditStatusListener?=null
    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.EditSearchView)
        mDefaultBackgroundColor = typedArray.getResourceId(R.styleable.EditSearchView_edit_search_default_background, mDefaultBackgroundColor)
        mFocusBackgroundColor = typedArray.getResourceId(R.styleable.EditSearchView_edit_search_default_background, mFocusBackgroundColor)
        hintText = typedArray.getString(R.styleable.EditSearchView_edit_search_text_hint)
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.EditSearchView_edit_search_text_size,mTextSize)
        mTextColor = typedArray.getColor(R.styleable.EditSearchView_edit_search_text_color,mTextColor)
        mTextHintColor = typedArray.getColor(R.styleable.EditSearchView_edit_search_text_hint_color,mTextHintColor)

        leftIconRes = typedArray.getResourceId(R.styleable.EditSearchView_edit_search_left_icon_res,leftIconRes)
        leftIconSize = typedArray.getDimensionPixelSize(R.styleable.EditSearchView_edit_search_left_icon_size,leftIconSize)
        leftIconPadding = typedArray.getDimensionPixelSize(R.styleable.EditSearchView_edit_search_left_icon_padding,leftIconPadding)


        delIconRes = typedArray.getResourceId(R.styleable.EditSearchView_edit_search_del_icon_res,delIconRes)
        delIconSize = typedArray.getDimensionPixelSize(R.styleable.EditSearchView_edit_search_del_icon_size,delIconSize)
        delIconPadding = typedArray.getDimensionPixelSize(R.styleable.EditSearchView_edit_search_del_icon_padding,delIconPadding)
        addChildView()

    }

    fun getEditTextView() = editView

    private fun addChildView(){
        val editLayoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        editView.layoutParams = editLayoutParams
        editView.setBackgroundColor(Color.TRANSPARENT)
        editView.hint = hintText
        editView.setTextColor(mTextColor)
        editView.setHintTextColor(mTextHintColor)
        editView.setPadding(leftIconSize,0,delIconRes,0)
        editView.addTextChangedListener(this)
        editView.setOnEditorActionListener(this)
        editView.isSingleLine = true
        this.addView(editView)


        val leftLayoutParams = LayoutParams(leftIconSize,leftIconSize)
        leftLayoutParams.addRule(CENTER_VERTICAL)
        leftView.layoutParams = leftLayoutParams
        leftView.setImageResource(leftIconRes)
        this.addView(leftView)

        val rightLayoutParams = LayoutParams(leftIconSize,leftIconSize)
        rightLayoutParams.addRule(CENTER_VERTICAL)
        rightLayoutParams.addRule(ALIGN_PARENT_RIGHT)
        delView.setImageResource(delIconRes)
        delView.layoutParams = rightLayoutParams
        this.addView(delView)

        editView.onFocusChangeListener = this
        setBackgroundResource(mDefaultBackgroundColor)

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun afterTextChanged(p0: Editable?) {
        val content = editView.text.toString()
        delView.visibility = if (content.isNullOrEmpty()) View.GONE else View.VISIBLE
        editListener?.afterTextChanged(p0)
    }

    override fun onFocusChange(p0: View?, p1: Boolean) {
        setBackgroundResource(if (p1) mFocusBackgroundColor else mDefaultBackgroundColor)
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        return editListener?.onEditorAction(actionId,editView.text.toString()) == true
    }
}