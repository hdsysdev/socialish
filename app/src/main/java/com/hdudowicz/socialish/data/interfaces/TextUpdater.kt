package com.hdudowicz.socialish.data.interfaces

import android.text.Editable
import android.text.TextWatcher

class TextUpdater(var text: String): TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        text = s.toString()
    }

    override fun afterTextChanged(s: Editable?) {}

}