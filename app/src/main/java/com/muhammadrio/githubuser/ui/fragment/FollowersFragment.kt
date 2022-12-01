package com.muhammadrio.githubuser.ui.fragment

import android.os.Bundle

class FollowersFragment : ConnectedPeopleFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getFollowers()
    }

    override fun observeData() {
        viewModel.followers.observe(viewLifecycleOwner) { followers ->
            setRecyclerViewItems(followers)
        }
    }

    override fun onRefreshData() {
        viewModel.refreshFollowers()
    }
}