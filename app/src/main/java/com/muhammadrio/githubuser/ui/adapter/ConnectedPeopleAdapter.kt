package com.muhammadrio.githubuser.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.muhammadrio.githubuser.ui.fragment.FollowersFragment
import com.muhammadrio.githubuser.ui.fragment.FollowingFragment

class ConnectedPeopleAdapter(
    fragmentManager: FragmentManager, lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager,lifecycle) {

    private val fragments = listOf(
        FollowersFragment(),
        FollowingFragment()
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}