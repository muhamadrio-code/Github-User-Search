package com.muhammadrio.githubuser.model

import com.muhammadrio.githubuser.DEFAULT_PER_PAGE

data class QueryParams(
    val query: String,
    val page: Int = 1,
    val perPage: Int = DEFAULT_PER_PAGE
)
