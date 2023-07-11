package com.example.weatherlocationapp.weatherDetails

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.weatherlocationapp.R
import com.example.weatherlocationapp.databinding.FragmentWeatherDetailsBinding
import com.example.weatherlocationapp.local.WeatherLocationDao
import com.example.weatherlocationapp.local.WeatherLocationDatabase
import com.example.weatherlocationapp.local.WeatherLocationModel


class WeatherDetailsFragment : Fragment() {

    private val application: Application by lazy {
        requireNotNull(this.activity).application
    }
    private val dataBase: WeatherLocationDao by lazy {
        WeatherLocationDatabase.getInstance(application).weatherLocationDao
    }

    private val viewModelFactory: WeatherDetailsViewModelFactory by lazy {
        WeatherDetailsViewModelFactory(dataBase)
    }
    private val viewModel: WeatherDetailsViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[WeatherDetailsViewModel::class.java]
    }

    private lateinit var binding :FragmentWeatherDetailsBinding
    private var weatherLocationItem : WeatherLocationModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_weather_details,container,false)
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)

         weatherLocationItem = WeatherDetailsFragmentArgs.fromBundle(requireArguments()).selectedWeatherLocation
        binding.item = weatherLocationItem

        viewModel.navigateBack.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigateUp()
                viewModel.navigateBackCompleted()
            }
        })

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.details_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.delete_item -> {
                weatherLocationItem?.let {
                 viewModel.deleteItem(it.id)
                }
            }
        }
        return true
    }

}