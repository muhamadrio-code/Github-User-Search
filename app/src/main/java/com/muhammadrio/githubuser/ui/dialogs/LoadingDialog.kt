package com.muhammadrio.githubuser.ui.dialogs

import android.content.Context
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AlertDialog
import com.muhammadrio.githubuser.databinding.DialogLoadingBinding

class LoadingDialog(context: Context) : AlertDialog(context) {

    init {
        val view = DialogLoadingBinding.inflate(layoutInflater)
        view.animationView.enableMergePathsForKitKatAndAbove(true)
        setView(view.root)
        setCancelable(false)
        window?.setBackgroundDrawable(ColorDrawable(0))
    }
}