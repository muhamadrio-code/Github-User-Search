package com.muhammadrio.githubuser.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.muhammadrio.githubuser.MainApplication
import com.muhammadrio.githubuser.databinding.LayoutUserListBinding
import com.muhammadrio.githubuser.model.User
import com.muhammadrio.githubuser.data.ErrorMessage
import com.muhammadrio.githubuser.ui.adapter.UserAdapter
import com.muhammadrio.githubuser.viewmodel.UserDetailsViewModel
import com.muhammadrio.githubuser.viewmodel.UserDetailsViewModelFactory

abstract class ConnectedPeopleFragment : Fragment() {

    private lateinit var binding: LayoutUserListBinding
    private val userAdapter: UserAdapter = UserAdapter()

    protected val viewModel: UserDetailsViewModel by viewModels(
        ownerProducer = { requireParentFragment() },
        factoryProducer = { UserDetailsViewModelFactory(
            (requireActivity().applicationContext as MainApplication).userRepository
        ) }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutUserListBinding.inflate(layoutInflater)
        setupRecyclerView()
        subscribeObserver()
        setupListener()
        return binding.root
    }

    private fun setupListener() {
        binding.refreshBtn.setOnClickListener {
            viewModel.refreshOnFailure()
            setLoading(true)
            setRefreshBtn(false)
        }
    }

    private fun setupRecyclerView() {
        binding.userRecyclerView.adapter = userAdapter
    }

    protected fun hideErrorMessage() {
        binding.tvMessage.isVisible = false
    }

    protected fun setLoading(visible: Boolean = true) {
        binding.loadingIndicator.isVisible = visible
        if (visible) hideErrorMessage()
    }

    protected fun setRefreshBtn(visible: Boolean = true) {
        binding.refreshBtn.isVisible = visible
        if (visible) hideErrorMessage()
    }

    protected fun showErrorMessage(errorMessage: ErrorMessage) {
        setLoading(false)
        setRefreshBtn(false)
        binding.tvMessage.isVisible = true
        binding.tvMessage.text = runCatching { getString(errorMessage.body) }.getOrNull()
    }

    protected fun setRecyclerViewItems(items: List<User>) {
        userAdapter.submitList(items)
        binding.userRecyclerView.isVisible = true
    }

    protected abstract fun subscribeObserver()

}