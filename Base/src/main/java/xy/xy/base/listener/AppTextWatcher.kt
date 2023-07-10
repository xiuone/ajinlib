package xy.xy.base.listener

import android.text.Editable

interface AppTextWatcher {
    fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    fun afterTextChanged(s: Editable?) {}
}