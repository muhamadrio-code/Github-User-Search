package com.muhammadrio.githubuser.ui.fragment

import com.muhammadrio.githubuser.R
import com.muhammadrio.githubuser.data.ErrorMessage
import com.muhammadrio.githubuser.data.Result

class FollowersFragment : ConnectedPeopleFragment() {

    override fun subscribeObserver() {
        setLoading(true)
        viewModel.followers.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Failure -> setRefreshBtn(true)
                is Result.Success -> {
                    val followers = result.value
                    if (followers.isEmpty()) {
                        showErrorMessage(
                            ErrorMessage(0, R.string.no_followers, 0)
                        )
                    } else {
                        setRecyclerViewItems(followers)
                        hideErrorMessage()
                    }
                }
            }
            setLoading(false)
        }
    }


}