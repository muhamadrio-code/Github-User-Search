package com.muhammadrio.githubuser.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.muhammadrio.githubuser.data.QueryStatus
import com.muhammadrio.githubuser.databinding.LayoutUserListBinding
import com.muhammadrio.githubuser.model.User
import com.muhammadrio.githubuser.ui.adapter.UserAdapter
import com.muhammadrio.githubuser.viewmodel.ConnectedPeopleViewModel

abstract class ConnectedPeopleFragment : Fragment() {

    private lateinit var binding: LayoutUserListBinding
    private lateinit var userAdapter: UserAdapter

    abstract val viewModel: ConnectedPeopleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString(LOGIN_ARG)?.let {
            viewModel.setUserLogin(it)
        } ?: throw NullPointerException("argument cannot null")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutUserListBinding.inflate(layoutInflater)
        userAdapter = UserAdapter(viewModel)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        subscribeObserver()
        setupListener()
        observeData()
    }

    private fun setupListener() {
        binding.refreshBtn.setOnClickListener {
            onRefreshData()
            loading(true)
            showRefreshBtn(false)
        }
    }

    private fun setupRecyclerView() {
        binding.userRecyclerView.adapter = userAdapter
        userAdapter.setOnItemClickListener {
            viewModel.requestNavigation(
                ConnectedPeopleFragmentDirections.actionGlobalDetailsFragment(it.login)
            )
        }
    }

    private fun hideErrorMessage() {
        binding.tvMessage.isVisible = false
    }

    private fun loading(visible: Boolean = true) {
        binding.loadingIndicator.isVisible = visible
    }

    private fun showRefreshBtn(visible: Boolean = true) {
        binding.refreshBtn.isVisible = visible
    }

    private fun showError(errorMessage: String?) {
        binding.tvMessage.isVisible = true
        binding.tvMessage.text = errorMessage
    }

    protected fun setRecyclerViewItems(items: List<User>) {
        userAdapter.submitList(items)
    }

    private fun showRecyclerView(visible: Boolean) {
        binding.userRecyclerView.isVisible = visible
    }

    private fun subscribeObserver() {
        viewModel.navDirection.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { direction ->
                findNavController().navigate(direction)
            }
        }

        viewModel.queryStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                is QueryStatus.OnEmpty -> {}
                is QueryStatus.OnSuccess -> {
                    hideErrorMessage()
                    loading(false)
                    showRefreshBtn(false)
                    showRecyclerView(true)
                }
                is QueryStatus.OnFailure -> {
                    val messageString =
                        runCatching { getString(status.errorMessage.body) }.getOrNull()
                    if (messageString == null || messageString.isEmpty() || messageString.isBlank()) {
                        showRefreshBtn(true)
                    } else {
                        showError(messageString)
                    }
                    loading(false)
                    showRecyclerView(false)
                }
                is QueryStatus.OnLoading -> {
                    loading(true)
                    hideErrorMessage()
                    showRecyclerView(false)
                }
            }
        }
    }

    protected abstract fun observeData()

    protected abstract fun onRefreshData()

    companion object {
        const val LOGIN_ARG = "login"
    }
}