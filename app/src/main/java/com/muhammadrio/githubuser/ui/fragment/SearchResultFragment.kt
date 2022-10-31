package com.muhammadrio.githubuser.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.muhammadrio.githubuser.R
import com.muhammadrio.githubuser.SearchResponse
import com.muhammadrio.githubuser.databinding.FragmentSearchResultBinding
import com.muhammadrio.githubuser.showToast
import com.muhammadrio.githubuser.ui.adapter.UserAdapter
import com.muhammadrio.githubuser.viewmodel.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchResultFragment : Fragment() {

    private lateinit var binding: FragmentSearchResultBinding
    private lateinit var adapter: UserAdapter
    private val viewModel : UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchResultBinding.inflate(layoutInflater)
        adapter = UserAdapter()
        setupRecyclerView()
        setupToolbar()
        subscribeObserver()
        return binding.root
    }

    private fun subscribeObserver(){
        viewModel.searchEvent.observe(viewLifecycleOwner){ event ->
            val content = event.getContentIfNotHandled() ?: return@observe

            lifecycleScope.launch(Dispatchers.Main.immediate){
                delay(1000L)
                hideLoading()
                when(content){
                    is SearchResponse.OnSuccess -> {
                        if(content.dataIsExist){
                            val users = content.data.items
                            binding.rvListResult.isVisible = true
                            adapter.submitList(users)
                        } else {
                            showErrorMessage(
                                tittle = getString(R.string.user_not_found_tittle),
                                message = getString(R.string.user_not_found)
                            )
                        }
                    }
                    is SearchResponse.OnServiceUnavailable ->
                        showErrorMessage(
                            tittle = getString(R.string.service_unavailable_tittle),
                            message = getString(R.string.service_unavailable)
                        )
                    is SearchResponse.OnValidationFailed ->
                        showErrorMessage(
                            tittle = getString(R.string.validation_failed_tittle),
                            message = getString(R.string.validation_failed)
                        )
                    is SearchResponse.OnUnknownError ->
                        showErrorMessage(
                            tittle = getString(R.string.unknown_tittle),
                            message = getString(R.string.unknown_message,content.errorCode)
                        )
                }
            }
        }
    }

    private fun showErrorMessage(tittle:String, message:String){
        binding.apply {
            binding.rvListResult.isVisible = false
            llErrorMessageContainer.visibility = View.VISIBLE
            tvErrorMessageTittle.text = tittle
            tvErrorMessage.text = message
        }
    }

    private fun hideLoading() {
        binding.progressCircular.visibility = View.GONE
    }

    private fun setupToolbar() {
       binding.toolbar.setNavigationOnClickListener {
           findNavController().popBackStack()
       }
    }

    private fun setupRecyclerView() {
        binding.rvListResult.adapter = adapter
        adapter.setOnItemClickListener {
            requireContext().showToast("CLicked")
        }
    }
}