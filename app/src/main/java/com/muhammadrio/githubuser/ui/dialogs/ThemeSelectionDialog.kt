package com.muhammadrio.githubuser.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.muhammadrio.githubuser.PreferencesKeys
import com.muhammadrio.githubuser.R
import com.muhammadrio.githubuser.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ThemeSelectionDialog : DialogFragment() {
    private val viewModel: ThemeSelectionDialogViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        getCurrentMode()
        subscribeObserver()
        return getDialogBuilder().create()
    }

    private fun getDialogBuilder() : AlertDialog.Builder {
        val themes = arrayOf(
            getString(R.string.default_system),
            getString(R.string.light),
            getString(R.string.dark)
        )

        return AlertDialog.Builder(requireContext()).apply {
            setTitle(getString(R.string.select_theme))
            setSingleChoiceItems(
                themes, viewModel.getCurrentPosition()
            ) { _, which ->
                viewModel.setCurrentPosition(which)
            }
            setCancelable(false)
            setNegativeButton(getString(R.string.cancel)) { _, _ ->
                dismiss()
            }
            setPositiveButton(getString(R.string.oke)) { _, _ ->
                viewModel.onSelectTheme()
            }
            setOnDismissListener{
                onDismiss(it)
            }
        }
    }


    private fun getCurrentMode() {
        lifecycleScope.launchWhenCreated {
            requireContext().dataStore.data.collectLatest { pref ->
                val mode = pref[PreferencesKeys.THEME_MODE] ?: 0
                viewModel.setCurrentMode(mode)
            }
        }
    }

    private fun subscribeObserver() {
        viewModel.mode.observe(requireParentFragment().viewLifecycleOwner) { mode ->
            if (mode == 0) return@observe
            lifecycleScope.launch {
                requireContext().dataStore.edit { pref ->
                    pref[PreferencesKeys.THEME_MODE] = mode
                }.also {
                    withContext(Dispatchers.Main.immediate){
                        dismiss()
                    }
                }
            }
        }
    }

}