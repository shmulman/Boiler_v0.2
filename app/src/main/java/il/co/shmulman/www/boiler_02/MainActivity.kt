package il.co.shmulman.www.boiler_02

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

// Global list for Log file and temperature value
var temperatureInt = 0
var logStringMutibleList: MutableList<String> = mutableListOf()

// Temperature definitions for color coding
var coldTemperatureMaxValue = 36
var okTemperatureMinValue = 37
var okTemperatureMaxValue = 40
var hotTemperatureMinValue = 41

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ReadSensorsBtn.setOnClickListener() {
            ConnectionStatus.text = "Loading sensors ..."

            // Local axillary variables initiation
            var dataLoaded200 = false
            var dataLoaded201 = false

            // Read sensors data via HTML file thread
            doAsync{
                val response200 = URL("http://10.100.102.200/").readText()
                dataLoaded200 = true
                uiThread {
                    printOutput(response200,"200")
                }
                ConnectionStatus.text = printDataLoadedStatus(dataLoaded200,dataLoaded201)
            }

            doAsync {
                val response201 = URL("http://10.100.102.201/").readText()
                dataLoaded201 = true
                uiThread {
                    printOutput(response201,"201")
                }
                ConnectionStatus.text = printDataLoadedStatus(dataLoaded200,dataLoaded201)
            }

            // Print the result, whether the HTML file was read
            ConnectionStatus.text = printDataLoadedStatus(dataLoaded200,dataLoaded201)
        }

        // Go to Log Activity and show the Log file
        ShowLogBtn.setOnClickListener {
            ConnectionStatus.text = "Read Log File"

            val intentToLog = Intent(applicationContext,ActivityLog::class.java)

            // Intent can not pass a List, it is converted to Array
            val logDataArrayList : ArrayList<String> = ArrayList(logStringMutibleList)
            intentToLog.putExtra("textLogData",logDataArrayList)

            startActivity(intentToLog)
        }

        SettingsBtn.setOnClickListener{
            ConnectionStatus.text = "Go to Settings"

            val intentToSettings = Intent(applicationContext,ActivitySettings::class.java)

            // Intent can not pass a List, it is converted to Array
            val logDataArrayList : ArrayList<String> = ArrayList(logStringMutibleList)
            intentToSettings.putExtra("textLogData",logDataArrayList)

            startActivity(intentToSettings)
        }
    }

    private fun printOutput(response : String, sensorNumber : String) {
        // Parse data from HTML file of the sensors
        var timeAndDate = response.substringAfter("<h1>").substringBefore("<br>").dropLast(1)
        var temperatureString = response.substringAfter("Temp").substringBefore(" C ")

        // Remove spaces from temperature string and "\n" from time string
        timeAndDate = timeAndDate.replace("\\n".toRegex(), "")
        temperatureString = temperatureString.replace("\\s".toRegex(), "")

        // Update the Logfile List
        logStringMutibleList.add("$sensorNumber $timeAndDate Temp: $temperatureInt\n")

        // Convert parsed temperature string to int
        val temperatureIntOrNull = temperatureString.toIntOrNull()
        if (temperatureIntOrNull == null) {
            ConnectionStatus.text = "Can not parse the temperature"
        } else {
            temperatureInt = temperatureIntOrNull
            // Save the temperature to SharedPreference
            val temperatureIntSharedPreference = getPreferences(MODE_PRIVATE)
            with (temperatureIntSharedPreference.edit()) {
                putInt("SharedPreferenceKeyTemperature",temperatureInt)
                apply()
            }
        }

        // Color coding for different temperatures
        when (temperatureInt) {
            in 0..coldTemperatureMaxValue -> {
                TemperatureText.setTextColor(Color.parseColor("#0000FF"))
                TemperatureText.text = "Cold"
            }
            in okTemperatureMinValue..okTemperatureMaxValue -> {
                TemperatureText.setTextColor(Color.parseColor("#00FF00"))
                TemperatureText.text = "Ok"
            }
            in hotTemperatureMinValue..100 -> {
                TemperatureText.setTextColor(Color.parseColor("#FF0000"))
                TemperatureText.text = "Hot"
            }
            else -> ConnectionStatus.text = "Temperature $temperatureInt is not feasible for water"
        }
        TemperatureValue.text = temperatureString + "\u00B0"

    }

    private fun printDataLoadedStatus(status1:Boolean,status2:Boolean):String {

        return when(status1){
            true    -> {
                if (status2) "Sensors 200 and 201 are loaded"
                else "Sensor 200 is loaded 201 is NOT"
            }
            false -> {
                if (status2) "Sensor 201 is loaded 200 is NOT"
                else "Can NOT read sensors 200 and 201"
            }
        }
    }
}
