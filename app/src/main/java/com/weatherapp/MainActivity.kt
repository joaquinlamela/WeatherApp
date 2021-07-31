package com.weatherapp

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import com.weatherapp.databinding.ActivityMainBinding
import com.weatherapp.repository.implementation.WeatherRepository
import com.weatherapp.ui.home.HomeViewModel
import com.weatherapp.ui.home.HomeViewModelFactory
import com.weatherapp.ui.map.MapViewModel
import com.weatherapp.ui.map.MapViewModelFactory

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private lateinit var binding: ActivityMainBinding
    private lateinit var homeVM: HomeViewModel
    private lateinit var mapVM: MapViewModel
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

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

        if(hasLocationPermission()){
            goToHome()
        }else{
            requestLocationPermission()
        }

    }

    private fun hasLocationPermission() = EasyPermissions.hasPermissions(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private fun requestLocationPermission() {
        EasyPermissions.requestPermissions(
            this,
            "This app need to get your location",
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if (perms.isNotEmpty()) {
                    goToHome()
                }
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(this).build().show()
        } else {
            requestLocationPermission()
        }
    }

    private fun goToHome(){
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