package com.muhammadrio.githubuser.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
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
import com.muhammadrio.githubuser.model.UserDetails
import com.muhammadrio.githubuser.network.QueryStatus
import com.muhammadrio.githubuser.ui.dialogs.LoadingDialog
import com.muhammadrio.githubuser.viewmodel.UserViewModel

class DetailsFragment : Fragment() {

    private lateinit var binding: FragmentDetailsBinding
    private val args: DetailsFragmentArgs by navArgs()
    private val viewModel: UserViewModel by viewModels()
    private lateinit var loadingDialog: LoadingDialog

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
        loadingDialog = LoadingDialog(requireActivity())
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
        binding.navigationBackBtn.setOnClickListener {
            popBackStack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.appbarLayout.removeOnOffsetChangedListener(offsetChangedListener)
    }

    private fun setupToolbar(title: String, subtitle: String) {
        binding.toolbarSubtitle.text = subtitle
        binding.toolbarTitle.text = title
    }

    private fun buildTextView(@StyleRes style: Int, text: String, @DrawableRes icon: Int): TextView {
        val tv = MaterialTextView(requireContext())
        tv.text = text
        tv.style(style)
        tv.setCompoundDrawablesRelativeWithIntrinsicBounds(icon, 0,0,0)
        return tv
    }

    private fun subscribeObserver() {
        viewModel.queryStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                is QueryStatus.OnEmpty -> {}
                is QueryStatus.OnSuccess -> {
                    loadingDialog.dismiss()
                }
                is QueryStatus.OnFailure -> {
                    Toast.makeText(requireContext(), getString(status.errorMessage.body), Toast.LENGTH_LONG).show()
                    popBackStack()
                }
                is QueryStatus.OnLoading -> {
                    loadingDialog.show()
                }
            }
        }

        viewModel.userDetails.observe(viewLifecycleOwner) { userDetails ->
            userDetails ?: return@observe
            setupContent(userDetails)
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
            if(link.isEmpty() or link.isBlank()) return@let null
            buildTextView(R.style.App_TextAppearance_Hyperlink, link,R.drawable.ic_link)
        }

        val companyTv = userDetails.company?.let { company ->
            if(company.isEmpty() or company.isBlank()) return@let null
            buildTextView(R.style.App_TextAppearance_Company, company,R.drawable.ic_company)
        }

        val twitterTv = userDetails.twitter_username?.let { twitter ->
            if(twitter.isEmpty() or twitter.isBlank()) return@let null
            buildTextView(R.style.App_TextAppearance_Company, twitter, R.drawable.ic_twitter)
        }

        val name = userDetails.name ?: userDetails.login
        val login = userDetails.login

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
        val subtitle = getString(R.string.repositories, userDetails.public_repos)
        name?.let { setupToolbar(title = name, subtitle = subtitle) }
    }

    private fun popBackStack() {
        findNavController().popBackStack()
    }

}