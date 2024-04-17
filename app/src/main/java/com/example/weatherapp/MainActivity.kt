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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
            // grab JSON objects from current
            val jsonObj = JSONObject(incomingJSON)
            val current = jsonObj.getJSONObject("current")
            val weather = current.getJSONArray("weather").getJSONObject(0)
            // grab JSON objects from daily position 1 (tomorrow)
            val daily1 = jsonObj.getJSONArray("daily").getJSONObject(1)
            val daily1Temp = daily1.getJSONObject("temp")
            val daily1Weather = daily1.getJSONArray("weather").getJSONObject(0)
            // grab JSON objects from daily position 2 (day after tomorrow)
            val daily2 = jsonObj.getJSONArray("daily").getJSONObject(2)
            val daily2Temp = daily2.getJSONObject("temp")
            val daily2Weather = daily2.getJSONArray("weather").getJSONObject(0)

            // getting all our data from current and assigning it to variables
            val weatherDescr = weather.getString("description")
            val currentTemp = current.getInt("feels_like")
            val humidity = current.getString("humidity")
            val wind = current.getString("wind_speed")
            val currentDate = current.getLong("dt")
            val sunrise = current.getLong("sunrise")
            val sunset = current.getLong("sunset")

            // getting data from tomorrow and assigning it to variables
            val tomDate = daily1.getLong("dt")
            val tomDescr = daily1Weather.getString("description")
            val tomTemp = daily1Temp.getInt("day")

            // getting data from tomorrow and assigning it to variables
            val twoDaysDate = daily2.getLong("dt")
            val twoDaysDescr = daily2Weather.getString("description")
            val twoDaysTemp = daily2Temp.getInt("day")

            // assigning data to the different text boxes for current weather
            _main_binding.tvToday.text = SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).format(Date(currentDate*1000))
            _main_binding.tvWeatherDescr.text = weatherDescr.capitalize()
            _main_binding.tvCurrentTemp.text = "$currentTemp\u2109"
            _main_binding.tvSunrise.text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise*1000))
            _main_binding.tvSunset.text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset*1000))
            _main_binding.tvHumidity.text = humidity
            _main_binding.tvWindSpeed.text = wind

            // assigning data to different textViews for tomorrow's weather
            _main_binding.tvTomorrow.text = SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).format(Date(tomDate*1000))
            _main_binding.tvTomDescr.text = tomDescr.capitalize()
            _main_binding.tvTomTemp.text = "$tomTemp\u2109"

            // assigning data to different textViews for day after tomorrow's weather
            _main_binding.tvTwoDaysDate.text = SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).format(Date(twoDaysDate*1000))
            _main_binding.tvTwoDaysDescr.text = twoDaysDescr.capitalize()
            _main_binding.tvTwoDaysTemp.text = "$twoDaysTemp\u2109"


        }
    }
}