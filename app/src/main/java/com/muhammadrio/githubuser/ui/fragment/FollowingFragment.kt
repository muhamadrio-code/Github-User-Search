package com.muhammadrio.githubuser.ui.fragment

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.muhammadrio.githubuser.MainApplication
import com.muhammadrio.githubuser.viewmodel.ConnectedPeopleViewModel
import com.muhammadrio.githubuser.viewmodel.UserViewModelFactory

class FollowingFragment : ConnectedPeopleFragment() {

    override val viewModel: ConnectedPeopleViewModel by viewModels {
        UserViewModelFactory(
            (requireActivity().applicationContext as MainApplication).userRepository
        )
    }

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