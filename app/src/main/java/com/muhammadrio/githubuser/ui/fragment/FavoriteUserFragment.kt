package com.muhammadrio.githubuser.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.muhammadrio.githubuser.MainApplication
import com.muhammadrio.githubuser.R
import com.muhammadrio.githubuser.data.ErrorMessage
import com.muhammadrio.githubuser.data.QueryStatus
import com.muhammadrio.githubuser.databinding.FragmentFavoriteUserBinding
import com.muhammadrio.githubuser.ui.adapter.UserAdapter
import com.muhammadrio.githubuser.viewmodel.FavoriteUserViewModel
import com.muhammadrio.githubuser.viewmodel.UserViewModelFactory

class FavoriteUserFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteUserBinding
    private lateinit var userAdapter: UserAdapter
    private val viewModel: FavoriteUserViewModel by viewModels {
        UserViewModelFactory(
            (requireActivity().applicationContext as MainApplication).userRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteUserBinding.inflate(layoutInflater)
        userAdapter = UserAdapter(viewModel)
        subscribeObserver()
        setupRecyclerView()
        setupToolbar()
        return binding.root
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.setOnMenuItemClickListener {
            if(it.itemId == R.id.clear_menu) {
                viewModel.requestClearAll()
            }

            true
        }
    }

    private fun setupRecyclerView() {
        binding.rcvFavUser.adapter = userAdapter
        userAdapter.setOnItemClickListener {
            viewModel.requestNavigation(
                FavoriteUserFragmentDirections.actionGlobalDetailsFragment(it.login)
            )
        }
    }

    private fun subscribeObserver() {
        viewModel.queryStatus.observe(viewLifecycleOwner) { status ->
            if (status is QueryStatus.OnFailure) {
                binding.tvDefaultMessage.isVisible = true
                binding.tvDefaultMessage.setText(status.errorMessage.body)
            }
        }

        viewModel.favoriteUsers.observe(viewLifecycleOwner) {
            if (it == null || it.isEmpty()) {
                viewModel.requestErrorState(
                    ErrorMessage(
                        0, R.string.favorite_user_empty, 0
                    )
                )
            }
            userAdapter.submitList(it)
        }

        viewModel.navDirection.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { directions ->
                findNavController().navigate(directions)
            }
        }

        viewModel.requestEvent.observe(viewLifecycleOwner) { requestEvent ->
            if (requestEvent) {
                val confirmDialog = AlertDialog.Builder(requireContext())
                    .setMessage(getString(R.string.confirmation_clear_all))
                    .setOnDismissListener {
                        viewModel.requestEventFinish()
                    }
                    .setOnCancelListener {
                        viewModel.requestEventFinish()
                    }
                    .setNegativeButton(getString(R.string.cancel)) { d, _ ->
                        d.dismiss()
                    }
                    .setPositiveButton(getString(R.string.oke)) { d, _ ->
                        viewModel.clearAllFavoriteUser()
                        d.dismiss()
                    }.create()
                confirmDialog.show()
            }
        }
    }

}