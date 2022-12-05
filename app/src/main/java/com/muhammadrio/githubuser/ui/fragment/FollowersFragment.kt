package com.muhammadrio.githubuser.ui.fragment

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.muhammadrio.githubuser.MainApplication
import com.muhammadrio.githubuser.viewmodel.ConnectedPeopleViewModel
import com.muhammadrio.githubuser.viewmodel.UserViewModelFactory

class FollowersFragment : ConnectedPeopleFragment() {

    override val viewModel: ConnectedPeopleViewModel by viewModels {
        UserViewModelFactory(
            (requireActivity().applicationContext as MainApplication).userRepository
        )
    }

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