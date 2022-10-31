package com.muhammadrio.githubuser.network

import androidx.annotation.StringRes

data class ErrorMessage(
    @StringRes val header:Int,
    @StringRes val body:Int,
    val code:Int
)