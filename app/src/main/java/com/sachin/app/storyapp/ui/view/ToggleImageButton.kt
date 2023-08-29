package com.sachin.app.storyapp.ui.view

import android.content.Context
import android.util.AttributeSet
import android.widget.Checkable
import androidx.appcompat.widget.AppCompatImageButton
import com.sachin.app.storyapp.R

class ToggleImageButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatImageButton(context, attrs, R.style.ToggleImageButton), Checkable {

    private var onCheckedChangeListener: OnCheckedChangeListener? = null

    fun setOnCheckChangeListener(listener: OnCheckedChangeListener) {
        onCheckedChangeListener = listener
        setOnClickListener { toggle() }
    }

    override fun setChecked(p0: Boolean) {
        isSelected = true
    }

    override fun isChecked(): Boolean {
        return isSelected
    }

    override fun toggle() {
        isSelected = !isSelected
        onCheckedChangeListener?.onCheckedChanged(isChecked)
    }

    fun interface OnCheckedChangeListener {
        fun onCheckedChanged(isChecked: Boolean)
    }
}