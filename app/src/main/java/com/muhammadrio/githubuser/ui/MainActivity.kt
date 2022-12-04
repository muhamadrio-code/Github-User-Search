package com.muhammadrio.githubuser.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.muhammadrio.githubuser.PreferencesKeys
import com.muhammadrio.githubuser.R
import com.muhammadrio.githubuser.dataStore
import kotlinx.coroutines.flow.collectLatest

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenCreated {
            dataStore.data.collectLatest { pref ->
                val mode = pref[PreferencesKeys.THEME_MODE] ?: AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                AppCompatDelegate.setDefaultNightMode(mode)
            }
        }
        setContentView(R.layout.activity_main)
    }
}