package com.muhammadrio.githubuser.ui.fragment

import android.app.SearchManager
import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.muhammadrio.githubuser.R
import com.muhammadrio.githubuser.databinding.FragmentSearchUserBinding
import com.muhammadrio.githubuser.network.QueryStatus
import com.muhammadrio.githubuser.provider.SuggestionProvider
import com.muhammadrio.githubuser.ui.adapter.UserAdapter
import com.muhammadrio.githubuser.ui.dialogs.LoadingDialog
import com.muhammadrio.githubuser.viewmodel.UserViewModel

class SearchUserFragment : Fragment(),
    SearchView.OnQueryTextListener,
    SearchView.OnSuggestionListener {

    private lateinit var binding: FragmentSearchUserBinding
    private lateinit var suggestionProvider: SearchRecentSuggestions
    private lateinit var searchView: SearchView
    private lateinit var loadingDialog: LoadingDialog
    private val viewModel: UserViewModel by viewModels()
    private lateinit var adapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchUserBinding.inflate(layoutInflater)
        setupToolbar()
        setupRecyclerView()
        subscribeObserver()
        loadingDialog = LoadingDialog(requireActivity())
        return binding.root
    }

    private fun setupToolbar() {
        val menuItem = binding.toolbar.menu.findItem(R.id.menu_search)
        searchView = menuItem?.actionView as SearchView
        val searchManager =
            requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val inputFilters = arrayOf(
            InputFilter.LengthFilter(256)
        )
        suggestionProvider =
            SearchRecentSuggestions(
                requireActivity(),
                SuggestionProvider.AUTHORITY,
                SuggestionProvider.MODE
            )
        searchView.findViewById<TextView>(R.id.search_src_text)?.filters = inputFilters
        searchView.setOnQueryTextListener(this)
        searchView.queryHint = requireContext().getString(R.string.search_hint)
        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
        searchView.setOnSuggestionListener(this)
    }

    private fun subscribeObserver() {
        viewModel.queryStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                is QueryStatus.OnEmpty -> {
                    showErrorMessage(
                        R.drawable.ic_box_empty,
                        R.string.no_data_tittle,
                        R.string.no_data_message
                    )
                }
                is QueryStatus.OnSuccess -> {
                    loadingDialog.dismiss()
                }
                is QueryStatus.OnFailure -> {
                    val message = status.errorMessage
                    showErrorMessage(
                        title = message.header,
                        message = message.body
                    )
                    loadingDialog.dismiss()
                }
                is QueryStatus.OnLoading -> {
                    hideErrorMessage()
                    hideKeyboard()
                    loadingDialog.show()
                }
            }
        }

        viewModel.users.observe(viewLifecycleOwner) { users ->
            users ?: return@observe
            binding.rcvUserList.isVisible = true
            adapter.submitList(users)
        }
    }

    private fun showErrorMessage(
        @DrawableRes icon: Int? = null,
        @StringRes title: Int,
        @StringRes message: Int
    ) {
        binding.apply {
            llErrorMessageContainer.isVisible = true
            rcvUserList.isVisible = false
            val iconDrawable = icon?.let {
                ContextCompat.getDrawable(requireContext(), it)
            }
            iconDrawable?.let {
                tvMessageHeader.setCompoundDrawablesRelativeWithIntrinsicBounds(null, it, null, null)
            }
            tvMessageHeader.text = runCatching { getString(title) }.getOrNull()
            tvMessageBody.text = runCatching { getString(message) }.getOrNull()
        }
    }

    private fun setupRecyclerView() {
        adapter = UserAdapter()
        binding.rcvUserList.adapter = adapter
        adapter.setOnItemClickListener {
            navigateToDetailsFragment(it.login)
        }
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
        binding.rcvUserList.layoutManager = layoutManager

        val scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val pastItemsVisible  = layoutManager.findFirstVisibleItemPosition()
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount

                if ((visibleItemCount + pastItemsVisible) >= totalItemCount) {
                    viewModel.searchNextPage()
                }
            }
        }
        binding.rcvUserList.addOnScrollListener(scrollListener)
    }

    override fun onSuggestionSelect(position: Int): Boolean {
        return true
    }

    override fun onSuggestionClick(position: Int): Boolean {
        val cursor = searchView.suggestionsAdapter.getItem(position) as Cursor
        val suggestion =
            cursor.getString(cursor.getColumnIndexOrThrow(SearchManager.SUGGEST_COLUMN_TEXT_1))
        searchView.setQuery(suggestion, false)
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        query ?: return false
        suggestionProvider.saveRecentQuery(query, null)
        viewModel.searchUsers(query)
        return true
    }

    private fun hideKeyboard() {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun hideErrorMessage() {
        binding.llErrorMessageContainer.isVisible = false
    }

    override fun onQueryTextChange(newText: String?): Boolean = false

    private fun navigateToDetailsFragment(userLogin:String) {
        val action = SearchUserFragmentDirections.actionSearchUserFragmentToDetailsFragment(userLogin)
        findNavController().navigate(action)
    }

}