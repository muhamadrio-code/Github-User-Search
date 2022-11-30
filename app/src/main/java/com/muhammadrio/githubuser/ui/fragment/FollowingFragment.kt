package com.muhammadrio.githubuser.ui.fragment

import com.muhammadrio.githubuser.R
import com.muhammadrio.githubuser.data.ErrorMessage
import com.muhammadrio.githubuser.data.Result

class FollowingFragment : ConnectedPeopleFragment() {
    override fun subscribeObserver() {
        setLoading(true)
        viewModel.following.observe(viewLifecycleOwner) { result ->
            when(result){
                is Result.Failure -> setRefreshBtn(true)
                is Result.Success -> {
                    val following = result.value
                    if (following.isEmpty()) {
                        showErrorMessage(
                            ErrorMessage(0, R.string.no_following, 0)
                        )
                    } else {
                        setRecyclerViewItems(following)
                        hideErrorMessage()
                    }
                }
            }
            setLoading(false)
        }
    }
}