package com.muhammadrio.githubuser.ui.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.muhammadrio.githubuser.ui.fragment.ConnectedPeopleFragment
import com.muhammadrio.githubuser.ui.fragment.FollowersFragment
import com.muhammadrio.githubuser.ui.fragment.FollowingFragment

class ConnectedPeopleAdapter(
    private val login:String,
    fragmentManager: FragmentManager, lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager,lifecycle) {

    private val fragments = listOf(
        FollowersFragment(),
        FollowingFragment()
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        val fragment = fragments[position]
        val bundle = Bundle().apply {
            putString(ConnectedPeopleFragment.LOGIN_ARG,login)
        }
        fragment.arguments = bundle
        return fragment
    }
}