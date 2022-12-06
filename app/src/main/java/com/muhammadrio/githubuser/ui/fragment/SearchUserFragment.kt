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
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.muhammadrio.githubuser.MAX_QUERY_LENGTH
import com.muhammadrio.githubuser.MainApplication
import com.muhammadrio.githubuser.R
import com.muhammadrio.githubuser.data.QueryStatus
import com.muhammadrio.githubuser.databinding.FragmentSearchUserBinding
import com.muhammadrio.githubuser.provider.SuggestionProvider
import com.muhammadrio.githubuser.ui.adapter.UserAdapter
import com.muhammadrio.githubuser.ui.dialogs.LoadingDialog
import com.muhammadrio.githubuser.ui.dialogs.ThemeSelectionDialog
import com.muhammadrio.githubuser.viewmodel.SearchUserViewModel
import com.muhammadrio.githubuser.viewmodel.UserViewModelFactory

class SearchUserFragment : Fragment(),
    SearchView.OnQueryTextListener,
    SearchView.OnSuggestionListener {

    companion object {
        const val THEME_SELECTION_TAG = "com.muhammadrio.ui.dialogs.ThemeSelectionDialog"
    }

    private lateinit var binding: FragmentSearchUserBinding
    private lateinit var suggestionProvider: SearchRecentSuggestions
    private lateinit var searchView: SearchView
    private lateinit var loadingDialog: LoadingDialog
    private val viewModel: SearchUserViewModel by viewModels {
        UserViewModelFactory((requireActivity().applicationContext as MainApplication).userRepository)
    }

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
        loadingDialog = LoadingDialog(requireContext())
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshUsers()
    }

    private fun setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.favorite_menu -> {
                    viewModel.requestNavigation(
                        SearchUserFragmentDirections.actionSearchUserFragmentToFavoriteUserFragment()
                    )
                }
                R.id.theme_menu -> viewModel.requestThemeSelectionDialog()
            }

            true
        }
        searchView = binding.searchBar
        val searchManager =
            requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val inputFilters = arrayOf(
            InputFilter.LengthFilter(MAX_QUERY_LENGTH)
        )
        suggestionProvider =
            SearchRecentSuggestions(
                requireActivity(),
                SuggestionProvider.AUTHORITY,
                SuggestionProvider.MODE
            )
        searchView.findViewById<TextView>(R.id.search_src_text)?.filters = inputFilters
        searchView.setOnQueryTextListener(this)
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
                    showRecyclerView(false)
                }
                is QueryStatus.OnSuccess -> {
                    loadingDialog.dismiss()
                    hideErrorMessage()
                    showRecyclerView(true)
                }
                is QueryStatus.OnFailure -> {
                    val message = status.errorMessage
                    showErrorMessage(
                        title = message.header,
                        message = message.body
                    )
                    loadingDialog.dismiss()
                    showRecyclerView(false)
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
            if (users.isEmpty()) showRecyclerView(false)
            adapter.submitList(users)
        }

        viewModel.navDirection.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { direction ->
                startNavigate(direction)
            }
        }

        viewModel.showSelectionThemeDialog.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {
                val themeSelectionDialog = ThemeSelectionDialog()
                themeSelectionDialog.showNow(childFragmentManager, THEME_SELECTION_TAG)
            }
        }
    }

    private fun showRecyclerView(visible:Boolean) {
        binding.rcvUserList.isVisible = visible
    }

    private fun showErrorMessage(
        @DrawableRes icon: Int = 0,
        @StringRes title: Int,
        @StringRes message: Int
    ) {
        binding.apply {
            llErrorMessageContainer.isVisible = true
            val iconDrawable =
                runCatching { ContextCompat.getDrawable(requireContext(), icon) }.getOrNull()
            tvMessageHeader.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                iconDrawable,
                null,
                null
            )
            tvMessageHeader.text = runCatching { getString(title) }.getOrNull()
            tvMessageBody.text = runCatching { getString(message) }.getOrNull()
        }
    }

    private fun setupRecyclerView() {
        adapter = UserAdapter(viewModel)
        binding.rcvUserList.adapter = adapter
        binding.rcvUserList.itemAnimator = null
        adapter.setOnItemClickListener {
            viewModel.requestNavigation(
                SearchUserFragmentDirections.actionGlobalDetailsFragment(it.login)
            )
        }
        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rcvUserList.layoutManager = layoutManager

        val scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val pastItemsVisible = layoutManager.findFirstVisibleItemPosition()
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
        return false
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
        if (query.isBlank() or query.isEmpty()) return false
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

    private fun startNavigate(directions: NavDirections) {
        findNavController().navigate(directions)
    }
}