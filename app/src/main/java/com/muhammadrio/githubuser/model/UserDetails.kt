package com.muhammadrio.githubuser.model

import androidx.room.Ignore

data class UserDetails(
    val avatar_url: String?,
    val bio: String?,
    val blog: String?,
    val company: String?,
    val created_at: String?,
    val email: String?,
    val events_url: String?,
    val followers: Int,
    val followers_url: String?,
    val following: Int,
    val following_url: String?,
    val gists_url: String?,
    val gravatar_id: String?,
    val hireable: Boolean,
    val html_url: String?,
    val id: Int,
    val location: String?,
    val login: String,
    val name: String?,
    val node_id: String?,
    val organizations_url: String?,
    val public_gists: Int,
    val public_repos: Int,
    val received_events_url: String?,
    val repos_url: String?,
    val site_admin: Boolean,
    val starred_url: String?,
    val subscriptions_url: String?,
    val twitter_username: String?,
    val type: String?,
    val updated_at: String?,
    val url: String?,
    var isFavorite:Boolean = false
) {
    @Ignore
    fun asUser() : User = User(
        id = this.id,
        login = this.login,
        avatarUrl = this.avatar_url ?: "",
        eventsUrl = this.events_url ?: "",
        followersUrl = this .followers_url ?: "",
        followingUrl = this.following_url ?: "",
        gistsUrl = this.gists_url ?: "",
        gravatarId = this.gravatar_id ?: "",
        htmlUrl = this.html_url ?: "",
        nodeId = this.node_id ?: "",
        organizationsUrl = this.organizations_url ?: "",
        receivedEventsUrl = this.received_events_url ?: "",
        reposUrl = this.repos_url ?: "",
        score = 0,
        starredUrl = this.starred_url ?: "",
        siteAdmin = this.site_admin,
        subscriptionsUrl = this.subscriptions_url ?: "",
        type = this.type ?: "",
        url = this.url ?: ""
    )
}