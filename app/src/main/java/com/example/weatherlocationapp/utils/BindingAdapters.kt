package com.example.weatherlocationapp.utils

import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherlocationapp.R
import com.example.weatherlocationapp.local.WeatherLocationModel
import com.example.weatherlocationapp.weatherLocationsList.WeatherLocationsListAdapter
import pl.droidsonroids.gif.GifImageView
import java.net.URL


@BindingAdapter("listData")
fun setRecycler(recyclerView: RecyclerView, items: List<WeatherLocationModel>?) {
    val adapter = recyclerView.adapter as WeatherLocationsListAdapter
    adapter.submitList(items)
}

@BindingAdapter("setLoadingStatus")
fun bindLoadingStatus(progressBar: ProgressBar, status : Boolean){
    if(status){
        progressBar.visibility = View.VISIBLE
    }else{
        progressBar.visibility = View.GONE
    }
}

@BindingAdapter("setWeatherImage")
fun bindingWeatherImage(imageView: GifImageView, item: WeatherLocationModel){
    if(item.weatherMain.isNullOrBlank())
        imageView.setImageResource(R.drawable.ic_weather_status)
   else {
       when(WeatherStatus.valueOf(item.weatherMain)){
           WeatherStatus.Clear -> imageView.setImageResource(R.drawable.ic_clear_sky)
           WeatherStatus.Rain -> imageView.setImageResource(R.drawable.ic_rain)
           WeatherStatus.Wind -> imageView.setImageResource(R.drawable.ic_wind)
           WeatherStatus.Clouds -> imageView.setImageResource(R.drawable.ic_clouds)
        }
    }

}

@BindingAdapter("setRecyclerImage")
fun setRecyclerImage(imageView: ImageView,url:String){
    val uri = Uri.parse(url)
    Glide.with(imageView.context).load(uri).placeholder(R.drawable.ic_loading).error(R.drawable.ic_error).into(imageView)
}