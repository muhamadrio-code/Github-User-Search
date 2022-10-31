package com.muhammadrio.githubuser.ui.dialogs

import android.content.Context
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AlertDialog
import com.muhammadrio.githubuser.databinding.DialogLoadingBinding

class LoadingDialog(context: Context) : AlertDialog(context) {
    override fun show() {
        val view = DialogLoadingBinding.inflate(layoutInflater)
        setView(view.root)
        setCancelable(false)
        window?.setBackgroundDrawable(ColorDrawable(0))
        super.show()
    }
}