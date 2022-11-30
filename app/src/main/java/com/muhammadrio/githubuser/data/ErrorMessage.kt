package com.muhammadrio.githubuser.data

import androidx.annotation.StringRes

data class ErrorMessage(
    @StringRes val header:Int,
    @StringRes val body:Int,
    val code:Int
)