package com.sachin.app.storyapp.core.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder


open class MaterialDialogFragment(@LayoutRes layoutResId: Int) : DialogFragment(layoutResId) {

    private var _dialogView: View? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireActivity())
        _dialogView = onCreateView(layoutInflater, null, savedInstanceState)
        builder.setView(_dialogView)
        builder.setCancelable(false)
        return builder.create()
    }

    override fun getView(): View? {
        return _dialogView
    }
}