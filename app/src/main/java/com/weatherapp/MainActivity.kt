package com.weatherapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.weatherapp.databinding.ActivityMainBinding
import com.weatherapp.repository.implementation.WeatherRepository
import com.weatherapp.ui.home.HomeViewModel
import com.weatherapp.ui.home.HomeViewModelFactory
import com.weatherapp.ui.map.MapViewModel
import com.weatherapp.ui.map.MapViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var homeVM: HomeViewModel
    private lateinit var mapVM: MapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val homeViewModelFactory = HomeViewModelFactory(
            WeatherRepository()
        )

        homeVM =
            ViewModelProvider(this, homeViewModelFactory).get(HomeViewModel::class.java)


        val mapViewModelFactory = MapViewModelFactory(
            WeatherRepository()
        )

        mapVM =
            ViewModelProvider(this, mapViewModelFactory).get(MapViewModel::class.java)

        goToHome()
    }


    private fun goToHome() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_map
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

}