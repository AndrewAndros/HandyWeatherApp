package com.androsgames.myweatherapp.ui

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androsgames.myweatherapp.R
import com.androsgames.myweatherapp.adapters.WeatherReportAdapter
import com.androsgames.myweatherapp.database.AppDatabase
import com.androsgames.myweatherapp.models.CityId
import com.androsgames.myweatherapp.models.WeatherData
import com.androsgames.myweatherapp.utils.VerticalItemDecorator
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch


class MainActivity : BaseActivity(), WeatherReportAdapter.Interaction {

    override fun onItemSelected(position: Int, item: WeatherData) {
        passToWeatherActivity(item)
    }


    private lateinit var adapter : WeatherReportAdapter
    lateinit var viewModel: MainViewModel
    val TAG : String = "TEST: "
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        initRecyclerView()
        subscribeObservers()

        if(!viewModel.firstLaunchOfActivityCompleted) {
            getCurrentLocation()
            viewModel.requestCachedCities()
            showProgressBar(true)
        }

        add_city_bn.setOnClickListener {
            search_layout.visibility = View.VISIBLE
            search_input.requestFocus()
        }

        search_confirm_bn.setOnClickListener {
            if(search_input.text.toString().isNotBlank()) {
                viewModel.getWeatherByCityName(search_input.text.toString())
                search_input.text.clear()
                search_input.clearFocus()
                search_layout.visibility = View.GONE
                showProgressBar(true)
            } else {
                showToastMessage("Input is empty")
            }
        }

        cancel_bn.setOnClickListener {
            search_input.text.clear()
            search_input.clearFocus()
            search_layout.visibility = View.GONE
        }
    }


    private fun initRecyclerView() {
        adapter = WeatherReportAdapter(this)
        val itemDecorator = VerticalItemDecorator(2)
        weather_recycler_view.addItemDecoration(itemDecorator)
        weather_recycler_view.layoutManager = LinearLayoutManager(this)
        var itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(weather_recycler_view)
        weather_recycler_view.adapter = adapter
    }


    private fun subscribeObservers() {
        viewModel.weatherReportsList.observe(this, Observer {reportsList ->
            adapter.submitList(reportsList)
            if(reportsList != null) {
                showProgressBar(false)
            }
        })


        viewModel.getErrorMessage().observe(this, Observer {errorReport ->
            if(errorReport != null && errorReport.isNotBlank()) {
                showToastMessage(errorReport)
                viewModel.consumeErrorMessage()
                showProgressBar(false)
            }

        })
    }


    private fun passToWeatherActivity(data: WeatherData) {
        val intent = Intent(this, WeatherActivity::class.java)
        intent.putExtra("report", data)
        startActivity(intent)
    }


    private fun getCurrentLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                location?.let {
                        viewModel.getWeatherByCoordinates(location.latitude, location.longitude)
                }
                // Показывать данное сообщение только при пустом листе, иначе вероятно город местонахождения уже есть в базе данных
                if (location == null && viewModel.weatherReportsList.value == null) {
                    showToastMessage("Failure to obtain weather in your location. Turn on GPS and Network connection")
                }
            }
    }


    private fun showToastMessage(message : String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()

    }


    val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }


        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            deleteFromDatabase(viewHolder.adapterPosition)
            deleteFromList(viewHolder.adapterPosition)
        }

    }

    private fun deleteFromDatabase(position : Int) {
        // баг во взаимодействии ItemTouchHelper  с адаптером, который не удалось решить,
        // при удалении последнего элемента индекс равнялся размеру листа (java.lang.IndexOutOfBoundsException: Invalid index 4, size is 4)
        // поэтому добавил дополнительные проверки сюда.
        var changedPosition = position
        if (position >= viewModel.weatherReportsList.value!!.size) {
            changedPosition = viewModel.weatherReportsList.value!!.size - 1
        }
        val cityId= CityId(viewModel.weatherReportsList.value!!.get(changedPosition).id)
        CoroutineScope(IO).launch {
            val cityDAO = AppDatabase.getInstance(applicationContext).cityDao()
            cityDAO.deleteCityFromDbCache(cityId)
        }
    }

    private fun deleteFromList(position : Int) {
        var changedPosition = position
        if (position >= viewModel.weatherReportsList.value!!.size) {
            changedPosition = viewModel.weatherReportsList.value!!.size - 1
        }
        viewModel.weatherReportsList.value!!.removeAt(changedPosition)
        adapter.submitList(viewModel.weatherReportsList.value!!)
    }


    override fun onDestroy() {
        super.onDestroy()
        viewModel.cancelJobs()
    }

   override fun onBackPressed() {
       // Android Extensions почему то здесь не работает в проверке условия (интересно)
       if(findViewById<CardView>(R.id.search_layout).visibility != View.VISIBLE) {
           super.onBackPressed()
       } else {
           search_input.text.clear()
           search_input.clearFocus()
           search_layout.visibility = View.GONE
       }
    }
}
