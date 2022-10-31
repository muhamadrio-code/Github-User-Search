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
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.muhammadrio.githubuser.R
import com.muhammadrio.githubuser.SearchResponse
import com.muhammadrio.githubuser.databinding.FragmentSearchUserBinding
import com.muhammadrio.githubuser.provider.SuggestionProvider
import com.muhammadrio.githubuser.showSnackBar
import com.muhammadrio.githubuser.showToast
import com.muhammadrio.githubuser.viewmodel.UserViewModel

class SearchUserFragment : Fragment(),
    SearchView.OnQueryTextListener,
    SearchView.OnSuggestionListener
{

    private lateinit var binding: FragmentSearchUserBinding
    private lateinit var suggestionProvider: SearchRecentSuggestions
    private lateinit var searchView: SearchView
    private val viewModel : UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchUserBinding.inflate(layoutInflater)
        setupToolbar()
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
            SearchRecentSuggestions(requireActivity(), SuggestionProvider.AUTHORITY, SuggestionProvider.MODE)
        searchView.findViewById<TextView>(R.id.search_src_text)?.filters = inputFilters
        searchView.setOnQueryTextListener(this)
        searchView.queryHint = requireContext().getString(R.string.search_hint)
        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
        searchView.setOnSuggestionListener(this)
    }

    override fun onSuggestionSelect(position: Int): Boolean {
        return true
    }

    override fun onSuggestionClick(position: Int): Boolean {
        val cursor = searchView.suggestionsAdapter.getItem(position) as Cursor
        val suggestion = cursor.getString(cursor.getColumnIndexOrThrow(SearchManager.SUGGEST_COLUMN_TEXT_1))
        searchView.setQuery(suggestion,false)
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        query ?: return false
        suggestionProvider.saveRecentQuery(query,null)
        viewModel.searchUsers(query)
        navigateToFragmentResult()
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean = false

    private fun navigateToFragmentResult(){
        findNavController().navigate(R.id.action_searchUserFragment_to_searchResultFragment)
    }
}