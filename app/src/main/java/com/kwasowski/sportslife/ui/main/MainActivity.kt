package com.kwasowski.sportslife.ui.main

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.kwasowski.sportslife.R
import com.kwasowski.sportslife.databinding.ActivityMainBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModel()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.topAppBar.setNavigationOnClickListener {
            Timber.d("NavigationOnClick")
        }
        binding.topAppBar.setOnMenuItemClickListener {
            onMenuItemClickListener(it)
        }
    }

    private fun onMenuItemClickListener(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.select_date -> {
                Timber.d("Select date")
                true
            }

            else -> false
        }
        return false
    }
}