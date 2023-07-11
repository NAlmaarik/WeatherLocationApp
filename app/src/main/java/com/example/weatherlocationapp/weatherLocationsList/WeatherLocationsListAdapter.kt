package com.example.weatherlocationapp.weatherLocationsList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherlocationapp.R
import com.example.weatherlocationapp.base.BaseRecyclerViewAdapter
import com.example.weatherlocationapp.databinding.WeatherItemBinding
import com.example.weatherlocationapp.local.WeatherLocationModel

class WeatherLocationsListAdapter(val onClickListener: OnClickListener): ListAdapter<WeatherLocationModel,WeatherLocationsListAdapter.ViewHolder>(DiffCallBack){
    object DiffCallBack: DiffUtil.ItemCallback<WeatherLocationModel>(){
        override fun areItemsTheSame(
            oldItem: WeatherLocationModel,
            newItem: WeatherLocationModel
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: WeatherLocationModel,
            newItem: WeatherLocationModel
        ): Boolean {
           return oldItem.id == newItem.id
        }

    }

    class ViewHolder(val binding : WeatherItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item : WeatherLocationModel){
            binding.item = item
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(WeatherItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(item)
        }
    }

    class OnClickListener(val clickListener : (weatherLocationModel:WeatherLocationModel) -> Unit){
        fun onClick(weatherLocationModel:WeatherLocationModel) = clickListener(weatherLocationModel)
    }
}