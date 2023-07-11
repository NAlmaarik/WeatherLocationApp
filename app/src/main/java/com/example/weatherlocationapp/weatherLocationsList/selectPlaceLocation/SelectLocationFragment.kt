package com.example.weatherlocationapp.weatherLocationsList.selectPlaceLocation

import android.Manifest
import android.app.AlertDialog
import android.app.Application
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.weatherlocationapp.R
import com.example.weatherlocationapp.databinding.FragmentSelectPlaceLocationBinding
import com.example.weatherlocationapp.domain.RequestWeatherLocation
import com.example.weatherlocationapp.local.WeatherLocationDao
import com.example.weatherlocationapp.local.WeatherLocationDatabase
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PointOfInterest
import java.util.Locale


class SelectLocationFragment : Fragment(), OnMapReadyCallback {

    private val application: Application by lazy {
        requireNotNull(this.activity).application
    }
    private val dataBase: WeatherLocationDao by lazy {
        WeatherLocationDatabase.getInstance(application).weatherLocationDao
    }

    private val viewModelFactory: SaveWeatherLocationViewModelFactory by lazy {
        SaveWeatherLocationViewModelFactory(dataBase)
    }
    private val viewModel: SaveWeatherLocationViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[SaveWeatherLocationViewModel::class.java]
    }

    private lateinit var binding: FragmentSelectPlaceLocationBinding
    private lateinit var map: GoogleMap
    private val REQUEST_LOCATION_PERMISSION = 1
    private var poi: PointOfInterest? = null
    private var latLong: LatLng? = null
    private lateinit var geoCoder: Geocoder
    private var currentDeviceLocation: LatLng? = null
    private val TAG = "SelectLocationFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_select_place_location,
            container,
            false
        )

        viewModel.navigateBack.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigateUp()
                viewModel.navigationCompleted()
            }
        })

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        geoCoder = Geocoder(requireContext(), Locale.getDefault())
        binding.saveButton.setOnClickListener { onLocationSelected() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        askUserToPickPOI()
        setOnLongClick(map)
        setOnPoiClick(map)
        enableLocationTracking()
    }

    private fun onLocationSelected() {
        if (poi != null) {
            setLocationValues(poi!!)
        } else if (latLong != null) {
            setLocationValues(latLong!!)
        } else {
            Toast.makeText(requireContext(), "Please select location", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setLocationValues(poi: PointOfInterest) {
        val result = geoCoder.getFromLocation(poi.latLng.latitude, poi.latLng.longitude, 1)
        if (result != null && result.size > 0) {
            if (result[0].locality.isNullOrBlank()) {
                val item = RequestWeatherLocation(
                    result[0].countryName,
                    checkCurrentLocation(
                        currentDeviceLocation,
                        result[0].countryName,
                        false
                    ),
                    poi.latLng.latitude,
                    poi.latLng.longitude
                )
                viewModel.validateAndSaveLocation(item)
            } else {
                val item = RequestWeatherLocation(
                    result[0].locality,
                    checkCurrentLocation(
                        currentDeviceLocation,
                        result[0].locality
                    ),
                    poi.latLng.latitude,
                    poi.latLng.longitude
                )
                viewModel.validateAndSaveLocation(item)
            }
        } else {
            Log.i(TAG, "Something went wrong setting location values")
        }
    }

    private fun setLocationValues(latLng: LatLng) {
        val result = geoCoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        if (result != null && result.size > 0) {
            if (result[0].locality.isNullOrBlank()) {
                val item = RequestWeatherLocation(
                    result[0].countryName,
                    checkCurrentLocation(currentDeviceLocation, result[0].countryName,false),
                    latLng.latitude,
                    latLng.longitude
                )
                viewModel.validateAndSaveLocation(item)

            } else {
                val item = RequestWeatherLocation(
                    result[0].locality,
                    checkCurrentLocation(currentDeviceLocation, result[0].locality),
                    latLng.latitude,
                    latLng.longitude
                )
                viewModel.validateAndSaveLocation(item)
            }
        } else {
            Log.i(TAG, "Something went wrong setting location values")
        }
    }

    private fun checkCurrentLocation(
        currentLocation: LatLng?,
        selectedLocation: String,
        cityFlag: Boolean = true
    ): Boolean {
        currentLocation?.let {
            val currentLocation = Geocoder(requireContext()).getFromLocation(it.latitude, it.longitude, 1)
            if (currentLocation != null && cityFlag)
                return currentLocation[0].locality == selectedLocation
            else if(currentLocation != null )
                return currentLocation[0].countryName == selectedLocation
        }
        return false

    }

    private fun enableLocationTracking() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        } else {
            map.setMyLocationEnabled(true)
            LocationServices.getFusedLocationProviderClient(requireContext()).lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    currentDeviceLocation = LatLng(location.latitude, location.longitude)
                    val currentDeviceLocation = LatLng(location.latitude, location.longitude)
                    val zoomLevel = 15f

                    map.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            currentDeviceLocation,
                            zoomLevel
                        )
                    )
                    map.addMarker(MarkerOptions().position(currentDeviceLocation))


                }

            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                enableLocationTracking()
            }
        }

    }

    private fun setOnLongClick(map: GoogleMap) {
        map.setOnMapLongClickListener { lanLong ->

            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                lanLong.latitude,
                lanLong.longitude
            )
            map.addMarker(
                MarkerOptions()
                    .title(getString(R.string.dropped_pin))
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .position(lanLong)
            )
            latLong = lanLong
            poi = null
        }
    }

    private fun setOnPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            val poiMarker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            poiMarker?.showInfoWindow()
            this.poi = poi
            latLong = null

        }
    }

    private fun askUserToPickPOI() {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.select_location)
            .setPositiveButton(R.string.ok, null)
            .create()
            .show()

    }

}
