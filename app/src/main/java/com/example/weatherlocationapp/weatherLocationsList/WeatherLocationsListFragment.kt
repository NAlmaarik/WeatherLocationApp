package com.example.weatherlocationapp.weatherLocationsList

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.weatherlocationapp.R
import com.example.weatherlocationapp.databinding.FragmentWeatherLocationsListBinding
import com.example.weatherlocationapp.local.WeatherLocationDao
import com.example.weatherlocationapp.local.WeatherLocationDatabase
import com.firebase.ui.auth.AuthUI

class WeatherLocationsListFragment : Fragment() {

    private val application: Application by lazy {
        requireNotNull(this.activity).application
    }
    private val dataBase: WeatherLocationDao by lazy {
        WeatherLocationDatabase.getInstance(application).weatherLocationDao
    }

    private val viewModelFactory: WeatherLocationsListViewModelFactory by lazy {
        WeatherLocationsListViewModelFactory(dataBase)
    }
    private val viewModel: WeatherLocationsListViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[WeatherLocationsListViewModel::class.java]
    }

    private lateinit var binding: FragmentWeatherLocationsListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_weather_locations_list,
            container,
            false
        )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.addPlaceButton.setOnClickListener {
            this.findNavController()
                .navigate(WeatherLocationsListFragmentDirections.actionWeatherLocationsListFragmentToSelectLocationFragment())
        }
        binding.weatherPlacesList.adapter =
            WeatherLocationsListAdapter(WeatherLocationsListAdapter.OnClickListener { item ->
                viewModel.navToDetails(item)
            })

        // check if list is empty show empty status else hide it
        viewModel.weatherLocationsList.observe(viewLifecycleOwner, Observer {
            if (it.isNullOrEmpty()) {
                binding.weatherPlacesList.visibility = View.GONE
                binding.noDataTextView.visibility = View.VISIBLE
            } else {
                binding.noDataTextView.visibility = View.GONE
                binding.weatherPlacesList.visibility = View.VISIBLE
            }
        }
        )

        viewModel.navigateToDetails.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(
                    WeatherLocationsListFragmentDirections.actionWeatherLocationsListFragmentToWeatherDetailsFragment(
                        it
                    )
                )
                viewModel.navToDetailsCompleted()
            }
        })

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_all -> {
                viewModel.deleteAll()
            }

            R.id.log_out -> {
                AuthUI.getInstance().signOut(requireContext()).addOnSuccessListener {
                    findNavController().navigate(WeatherLocationsListFragmentDirections.actionWeatherLocationsListFragmentToLogInFragment())
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

}