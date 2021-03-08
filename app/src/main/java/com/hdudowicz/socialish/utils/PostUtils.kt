package com.hdudowicz.socialish.utils

import android.content.Context
import androidx.databinding.ViewDataBinding

object PostUtils {
    // Extension function getting context from DataBinding classes
    fun ViewDataBinding.getContext(): Context{
        return root.context
    }

}