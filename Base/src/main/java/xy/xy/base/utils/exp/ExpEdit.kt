package xy.xy.base.utils.exp

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import xy.xy.base.R
import xy.xy.base.utils.AccountType


fun EditText.setAccountEdit(accountType: AccountType = AccountType.PHONE) {
    when(accountType){
        AccountType.PHONE->{
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_CLASS_PHONE
            hint = context?.getResString(R.string.please_phone)
        }
        AccountType.EMAIL->{
            inputType = InputType.TYPE_CLASS_TEXT
            hint = context?.getResString(R.string.please_email)
        }
        AccountType.PHONE_EMAIL->{
            inputType = InputType.TYPE_CLASS_TEXT
            hint = context?.getResString(R.string.please_account)
        }
    }
}



fun EditText.maxNumber(textView:TextView?,max:Int?,rule:String?) {
    if (textView == null || max == null || rule == null)return
    val length = text.toString().length
    textView.text = String.format(rule,length,max)
    addTextChangedListener(object :TextWatcher{
        override fun afterTextChanged(editable: Editable?) {
            if (editable == null)return
            val length = editable.length
            if (length > max){
                editable.delete(max, editable.length)
                textView.text = String.format(rule,max,max)
                return
            }
            textView.text = String.format(rule,length,max)
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    })
}