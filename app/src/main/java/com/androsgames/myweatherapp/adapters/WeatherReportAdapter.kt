package com.androsgames.myweatherapp.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.androsgames.myweatherapp.R
import com.androsgames.myweatherapp.models.WeatherData
import com.androsgames.myweatherapp.utils.WeatherUtils.convertTempFromKeltoCel
import com.androsgames.myweatherapp.utils.WeatherUtils.getIconByCode
import com.androsgames.myweatherapp.utils.WeatherUtils.nightTime
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.weather_list_item.view.*

class WeatherReportAdapter(private val interaction: Interaction) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mWeatherReports : ArrayList<WeatherData>? = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return WeatherReportViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.weather_list_item,
                parent,
                false
            ),
            interaction
        )
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is WeatherReportViewHolder -> {
                holder.bind(mWeatherReports!![position])
            }
        }
    }




    override fun getItemCount(): Int {
       if(mWeatherReports != null) {
           return mWeatherReports!!.size
       }
        return 0
    }

    fun submitList(list: List<WeatherData>) {
        mWeatherReports!!.clear()
        mWeatherReports!!.addAll(list)
        notifyDataSetChanged()
    }

    class WeatherReportViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?
    ) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: WeatherData) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }

            item_location.text = item.name
            item_temp.text =  "${convertTempFromKeltoCel(item.main.temp)}℃"

            Glide.with(this)
                .load(getIconByCode(item.weather.get(0).icon))
                .into(itemView.findViewById(R.id.item_picture))

            if(nightTime(item)) {
                this.background = ColorDrawable(
                    ContextCompat.getColor(this.context, R.color.colorDusk))
            } else {
                this.background = ColorDrawable(
                    ContextCompat.getColor(this.context, R.color.colorBlue))
            }
        }

    }


    interface Interaction {
        fun onItemSelected(position: Int, item: WeatherData)
    }
}