package com.woynapp.aliskanlik.presentation

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.woynapp.aliskanlik.R
import com.woynapp.aliskanlik.core.utils.Constants
import com.woynapp.aliskanlik.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        val mSharedPreferences =
            getSharedPreferences(Constants.PREFERENCE_DATABASE_NAME, Context.MODE_PRIVATE)
        val isDarkMode = mSharedPreferences.getBoolean(Constants.PREFERENCE_DARK_MODE, false)
        if (isDarkMode)
            setTheme(R.style.Theme_Aliskanlik_Dark)
        else
            setTheme(R.style.Theme_Aliskanlik_Light)
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostController =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostController.findNavController()
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}