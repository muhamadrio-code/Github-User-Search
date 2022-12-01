package com.muhammadrio.githubuser.ui.fragment

import android.os.Bundle

class FollowingFragment : ConnectedPeopleFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getFollowing()
    }

    override fun observeData() {
        viewModel.following.observe(viewLifecycleOwner) { following ->
            setRecyclerViewItems(following)
        }
    }

    override fun onRefreshData() {
        viewModel.refreshFollowing()
    }
}