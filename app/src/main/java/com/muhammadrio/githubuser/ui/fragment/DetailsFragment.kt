package com.muhammadrio.githubuser.ui.fragment

import android.os.Build
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import coil.transform.CircleCropTransformation
import com.airbnb.paris.extensions.style
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.textview.MaterialTextView
import com.muhammadrio.githubuser.R
import com.muhammadrio.githubuser.databinding.FragmentDetailsBinding
import com.muhammadrio.githubuser.network.Result
import com.muhammadrio.githubuser.viewmodel.UserViewModel

class DetailsFragment : Fragment() {

    private lateinit var binding: FragmentDetailsBinding
    private val args: DetailsFragmentArgs by navArgs()
    private val viewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(layoutInflater)
        subscribeObserver()
        binding.navigationBackBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        val listener = AppBarLayout.OnOffsetChangedListener { appBar, verticalOffset ->
            val seekProgress = -verticalOffset / appBar.totalScrollRange.toFloat()
            binding.motionLayoutToolbar.progress = seekProgress
            binding.profileMotionLayout.progress = seekProgress
        }
        binding.appbarLayout.addOnOffsetChangedListener(listener)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getUserDetails(args.userLogin)
    }

    private fun setupToolbar(name: String?, publicRepo: Int) {
        binding.toolbarSubtitle.text = getString(R.string.repositories, publicRepo)
        name?.let {
            binding.toolbarTitle.text = it
        }
    }

    private fun buildTextView(@StyleRes style: Int, text: String, @DrawableRes icon: Int): TextView {
        val tv = MaterialTextView(requireContext())
        tv.text = text
        tv.style(style)
        tv.setCompoundDrawablesRelativeWithIntrinsicBounds(icon, 0,0,0)
        return tv
    }

    private fun subscribeObserver() {
        viewModel.userDetails.observe(viewLifecycleOwner) { queryStatus ->
            with(binding) {
                when (queryStatus) {
                    is UserViewModel.QueryStatus.OnEmpty -> {}
                    is UserViewModel.QueryStatus.OnFinished -> {
                        val result = queryStatus.result
                        if (result is Result.Success) {
                            val value = result.value
                            val bioTv = value.bio?.let { bio ->
                                buildTextView(R.style.App_TextAppearance_Bio, bio, 0)
                            }

                            val followerFollowingTv = buildTextView(
                                R.style.App_TextAppearance_FollowersFollowing,
                                getString(
                                    R.string.followers_following,
                                    value.followers,
                                    value.following
                                ),
                                R.drawable.ic_people
                            )
                            val repositoriesTv = buildTextView(
                                R.style.App_TextAppearance_Repositories,
                                getString(R.string.repositories, value.public_repos),
                                R.drawable.ic_repository
                            )

                            val blogLinkTv = value.blog?.let { link ->
                                buildTextView(R.style.App_TextAppearance_Hyperlink, link,R.drawable.ic_link)
                            }

                            val companyTv = value.company?.let { company ->
                                buildTextView(R.style.App_TextAppearance_Company, company,R.drawable.ic_company)
                            }

                            val twitterTv = value.twitter_username?.let { twitter ->
                                buildTextView(R.style.App_TextAppearance_Company, twitter, R.drawable.ic_twitter)
                            }

                            detailContainer.apply {
                                removeAllViews()
                                bioTv?.let { addView(it) }
                                addView(followerFollowingTv)
                                addView(repositoriesTv)
                                blogLinkTv?.let { addView(it) }
                                companyTv?.let { addView(it) }
                                twitterTv?.let { addView(it) }
                            }

                            tvUserLogin.text = value.login
                            tvUserName.text = value.name
                            ivProfilePhoto.load(value.avatar_url) {
                                transformations(CircleCropTransformation())
                            }
                            setupToolbar(value.name, value.public_repos)
                        }
                    }
                    is UserViewModel.QueryStatus.OnLoading -> {}
                }

            }
        }
    }

}