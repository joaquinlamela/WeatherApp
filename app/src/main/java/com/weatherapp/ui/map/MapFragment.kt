package com.weatherapp.ui.map

import android.content.ContentValues
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.weatherapp.R
import com.weatherapp.databinding.FragmentMapBinding


class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private var map: GoogleMap? = null
    private var lastKnownLocation: Location? = null
    private val DEFAULT_ZOOM = 13
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val defaultLocation = LatLng(-34.905895, -56.164993)
    private var longitude: Float = -56.164993f
    private var latitude: Float = -34.905895f
    private var marker: Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        val mMapFragment = SupportMapFragment.newInstance()
        childFragmentManager.beginTransaction().add(R.id.map, mMapFragment).commit()
        mMapFragment.view?.isClickable = true
        mMapFragment.getMapAsync(this)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        updateLocationUI()
        getDeviceLocation()

        map!!.setOnMapLongClickListener(OnMapLongClickListener { latLng ->




            if (marker == null) {
                marker = map!!.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title("Your marker title")
                        .snippet("Your marker snippet")
                )
            } else {
                marker!!.setPosition(latLng)
            }
        })
    }

    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            map?.isMyLocationEnabled = true
            map?.uiSettings?.isMyLocationButtonEnabled = true
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun getDeviceLocation() {
        try {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    lastKnownLocation = task.result
                    if (lastKnownLocation != null) {
                        map?.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    lastKnownLocation!!.latitude,
                                    lastKnownLocation!!.longitude
                                ), DEFAULT_ZOOM.toFloat()
                            )
                        )
                        longitude = lastKnownLocation!!.longitude.toFloat()
                        latitude = lastKnownLocation!!.latitude.toFloat()
                    }
                } else {
                    Log.d(ContentValues.TAG, "Current location is null. Using defaults.")
                    Log.e(ContentValues.TAG, "Exception: %s", task.exception)
                    map?.moveCamera(
                        CameraUpdateFactory
                            .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat())
                    )
                    map?.uiSettings?.isMyLocationButtonEnabled = false
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
}