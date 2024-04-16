package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.weatherapp.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var _main_binding: ActivityMainBinding
    private lateinit var response: String

    private val LAT = 42.33
    private val LON = -88.26
    private val API = "6caa57ae7476ab3d3408fb161b2c0ab7"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _main_binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_main_binding.root)

        CoroutineScope(IO).launch{
            callAPI()

        }
    }

    private suspend fun callAPI(){
        val result = getResultFromAPI()
        populateInfo(result)
    }


    private suspend fun getResultFromAPI(): String {
        // make URL call
        // https://api.openweathermap.org/data/3.0/onecall?lat={lat}&lon={lon}&exclude={part}&appid={API key}
        try{
            val myURL = "https://api.openweathermap.org/data/3.0/onecall?lat=$LAT&lon=$LON&appid=$API&exclude=minutely,hourly&units=imperial"
            response = URL(myURL).readText(
                Charsets.UTF_8
            )

        } catch (e: java.lang.Exception){
            response = "ERR: $e"
        }
        // we got a good response, pass it back up
        return response
    }

    private suspend fun populateInfo(incomingJSON: String) {
        withContext(Main){
            val jsonObj = JSONObject(incomingJSON)
            val current = jsonObj.getJSONObject("current")
            val daily = jsonObj.getJSONArray("daily")
            val today = daily.getJSONObject(0)
            val weather = today.getJSONArray("weather")
            //val weather = daily.getJSONObject(0).getJSONObject("weather")

            // getting all our data from current and assigning it to variables
            val temp = current.getString("feels_like")
            val humidity = current.getString("humidity")
            val wind = current.getString("wind_speed")

            // getting data from daily and assigining it to variables
            // val minTemp = daily.getString("temp.min")
            // val maxTemp = daily.getString("temp.max")

            _main_binding.tvCurrentTemp.text = temp + "\u2109"

        }
    }
}