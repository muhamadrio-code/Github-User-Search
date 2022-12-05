package com.muhammadrio.githubuser.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import coil.transform.CircleCropTransformation
import com.airbnb.paris.extensions.style
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textview.MaterialTextView
import com.muhammadrio.githubuser.MainApplication
import com.muhammadrio.githubuser.R
import com.muhammadrio.githubuser.data.QueryStatus
import com.muhammadrio.githubuser.databinding.FragmentDetailsBinding
import com.muhammadrio.githubuser.model.UserDetails
import com.muhammadrio.githubuser.ui.adapter.ConnectedPeopleAdapter
import com.muhammadrio.githubuser.viewmodel.UserDetailsViewModel
import com.muhammadrio.githubuser.viewmodel.UserViewModelFactory

class DetailsFragment : Fragment() {

    private lateinit var binding: FragmentDetailsBinding
    private lateinit var vpAdapter: ConnectedPeopleAdapter
    private val args: DetailsFragmentArgs by navArgs()
    private val viewModel: UserDetailsViewModel by viewModels {
        UserViewModelFactory(
            (requireActivity().applicationContext as MainApplication).userRepository
        )
    }

    private var offsetChangedListener: AppBarLayout.OnOffsetChangedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getUserDetails(args.userLogin)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(layoutInflater)
        subscribeObserver()
        setupListener()
        return binding.root
    }

    private fun setupListener() {
        offsetChangedListener = AppBarLayout.OnOffsetChangedListener { appBar, verticalOffset ->
            val seekProgress = -verticalOffset / appBar.totalScrollRange.toFloat()
            binding.motionLayoutToolbar.progress = seekProgress
            binding.profileMotionLayout.progress = seekProgress
        }
        binding.appbarLayout.addOnOffsetChangedListener(offsetChangedListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.appbarLayout.removeOnOffsetChangedListener(offsetChangedListener)
    }

    private fun setupToolbar(title: String) {
        binding.toolbarTitle.text = title
        binding.toolbar.setNavigationOnClickListener {
            popBackStack()
        }
    }

    private fun buildTextView(
        @StyleRes style: Int,
        text: String,
        @DrawableRes icon: Int
    ): TextView {
        val tv = MaterialTextView(requireContext())
        tv.text = text
        tv.style(style)
        val linkify = Linkify.addLinks(tv, Linkify.WEB_URLS)
        if (linkify) {
            tv.movementMethod = LinkMovementMethod.getInstance()
        }

        tv.setCompoundDrawablesRelativeWithIntrinsicBounds(icon, 0, 0, 0)
        return tv
    }

    private fun subscribeObserver() {
        viewModel.userDetails.observe(viewLifecycleOwner) { userDetails ->
            userDetails ?: return@observe
            userDetails.login.let { login ->
                setupViewpager(login)
                setupTabLayout()
            }
            setupContent(userDetails)
        }

        viewModel.queryStatus.observe(viewLifecycleOwner) { status ->
            if (status is QueryStatus.OnFailure) {
                Toast.makeText(
                    requireContext(),
                    getString(status.errorMessage.body),
                    Toast.LENGTH_LONG
                ).show()
                popBackStack()
            }
        }
    }

    private fun setupContent(userDetails: UserDetails) {
        val bioTv = userDetails.bio?.let { bio ->
            buildTextView(R.style.App_TextAppearance_Bio, bio, 0)
        }

        val followerFollowingTv = buildTextView(
            R.style.App_TextAppearance_FollowersFollowing,
            getString(
                R.string.followers_following,
                userDetails.followers,
                userDetails.following
            ),
            R.drawable.ic_people
        )
        val repositoriesTv = buildTextView(
            R.style.App_TextAppearance_Repositories,
            getString(R.string.repositories, userDetails.public_repos),
            R.drawable.ic_repository
        )

        val blogLinkTv = userDetails.blog?.let { link ->
            if (link.isEmpty() or link.isBlank()) return@let null
            buildTextView(R.style.App_TextAppearance_Hyperlink, link, R.drawable.ic_link)
        }

        val companyTv = userDetails.company?.let { company ->
            if (company.isEmpty() or company.isBlank()) return@let null
            buildTextView(R.style.App_TextAppearance_Default, company, R.drawable.ic_company)
        }

        val twitterTv = userDetails.twitter_username?.let { twitter ->
            if (twitter.isEmpty() or twitter.isBlank()) return@let null
            buildTextView(R.style.App_TextAppearance_Default, twitter, R.drawable.ic_twitter)
        }

        val name = userDetails.name ?: userDetails.login
        val login = userDetails.login

        userDetails.html_url?.let { url ->
            binding.githubBtn1.alpha = 1f
            binding.githubBtn1.isEnabled = true
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())

            binding.githubBtn1.setOnClickListener {
                startActivity(intent)
            }
            binding.githubBtn2.setOnClickListener {
                startActivity(intent)
            }
        }

        binding.apply {
            detailContainer.apply {
                removeAllViews()
                bioTv?.let { addView(it) }
                addView(followerFollowingTv)
                addView(repositoriesTv)
                blogLinkTv?.let { addView(it) }
                companyTv?.let { addView(it) }
                twitterTv?.let { addView(it) }
            }

            tvUserLogin.text = login
            tvUserName.text = name
            ivProfilePhoto.load(userDetails.avatar_url) {
                placeholder(R.drawable.image_placeholder)
                transformations(CircleCropTransformation())
                error(R.drawable.ic_broken_image)
            }
        }

        setupToolbar(title = name)

    }

    private fun popBackStack() {
        findNavController().popBackStack()
    }

    private fun setupViewpager(login: String) {
        vpAdapter = ConnectedPeopleAdapter(login, requireActivity().supportFragmentManager, lifecycle)
        binding.viewPager.adapter = vpAdapter
    }

    private fun setupTabLayout() {
        val tabs = listOf(
            getString(R.string.followers),
            getString(R.string.following)
        )
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabs[position]
        }.attach()
    }

}