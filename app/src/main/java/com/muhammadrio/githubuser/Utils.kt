package com.muhammadrio.githubuser

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

fun Context.showToast(text:String){
    Toast.makeText(this,text,Toast.LENGTH_LONG).show()
}

fun Activity.showSnackBar(text: String){
    val snackbar = Snackbar.make(findViewById(android.R.id.content),text,Snackbar.LENGTH_INDEFINITE)
    snackbar.setAction(getString(R.string.default_snackbar_action)){
        snackbar.dismiss()
    }
    snackbar.show()
}