package il.co.shmulman.www.boiler_02

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

// Global variables
// Temperature definitions for color coding
var coldTemperatureMaxValue = 36
var okTemperatureMinValue = 37
var okTemperatureMaxValue = 40
var hotTemperatureMinValue = 41

// Global temperature value
var temperatureInt = 0
var printOutputTemperatureLast = 0
var printOutputTemperatureLow = 0
var printOutputTemperatureHigh = 0

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Define data set for Log file
        var dataForLogArrayList = ArrayList<String>()
        //Clear the previous Log data
        dataForLogArrayList.clear()

        // Clear ShearedPreferences
        val preferences = getSharedPreferences("myfile", 0)
        preferences.edit().remove("KeySet").commit()

        // Local axillary variables initiation
        var response200String = ""
        var response201String = ""
        var dataLoaded200 = false
        var dataLoaded201 = false

        ReadSensorsBtn.setOnClickListener() {
            ConnectionStatus.text = "Loading sensors ..."

            // Read sensors data via HTML file thread
            doAsync{
                val response200 = URL("http://10.100.102.200/").readText()
                dataLoaded200 = true
                uiThread {
                    // Main activity GUI update
                    response200String = printOutput(response200,"200")
                    //Data for Log file
                    dataForLogArrayList.add(response200String)
                }
                // Update status bar
                ConnectionStatus.text = printDataLoadedStatus(dataLoaded200,dataLoaded201)
            }

            doAsync {
                val response201 = URL("http://10.100.102.201/").readText()
                dataLoaded201 = true
                uiThread {
                    // Main activity GUI update
                    response201String = printOutput(response201,"201")
                    //Data for Log file
                    dataForLogArrayList.add(response201String)
                }
                // Update status bar
                ConnectionStatus.text = printDataLoadedStatus(dataLoaded200,dataLoaded201)
            }

            // Update status bar. Print the result, whether the HTML file was read
            ConnectionStatus.text = printDataLoadedStatus(dataLoaded200,dataLoaded201)
        }

        // Go to Log Activity and show the Log file
        ShowLogBtn.setOnClickListener {
            ConnectionStatus.text = "Read Log File"

            val intentToLog = Intent(applicationContext,ActivityLog::class.java)
            intentToLog.putStringArrayListExtra("LogData",dataForLogArrayList)
            startActivity(intentToLog)
        }

        // Go to Settings
        SettingsBtn.setOnClickListener{
            ConnectionStatus.text = "Go to Settings"

            val intentToSettings = Intent(applicationContext,ActivitySettings::class.java)
            startActivity(intentToSettings)
        }

        // Press "Read temperature" for the initiation
        ReadSensorsBtn.performClick()
    }

    private fun printOutput(response : String, sensorNumber : String) : String {

        // Parse data from HTML file of the sensors
        var timeAndDate = response.substringAfter("<h1>").substringBefore("<br>").dropLast(1)
        var temperatureString = response.substringAfter("Temp").substringBefore(" C ")

        // Remove spaces from temperature string and "\n" from time string
        timeAndDate = timeAndDate.replace("\\n".toRegex(), "")
        temperatureString = temperatureString.replace("\\s".toRegex(), "")

        // Convert parsed temperature string to int
        val temperatureIntOrNull = temperatureString.toIntOrNull()
        if (temperatureIntOrNull == null) {
            ConnectionStatus.text = "Can not parse the temperature"
        } else {
            temperatureInt = temperatureIntOrNull
        }

        // Compare to last temperature
        if (printOutputTemperatureLast == temperatureInt) {
            // Color coding for different temperatures and main activity GUI update
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
            TemperatureValue.text = "$temperatureString\u00B0"
            ConnectionStatus.text = "One value $temperatureInt°"
        } else {

            // Define high and low temperatures
            if (printOutputTemperatureLast > temperatureInt){
                printOutputTemperatureHigh = printOutputTemperatureLast
                printOutputTemperatureLow = temperatureInt
            } else {
                printOutputTemperatureHigh = temperatureInt
                printOutputTemperatureLow = printOutputTemperatureLast
            }

            // Color coding for different temperatures and main activity GUI update
            when (printOutputTemperatureHigh) {
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
            TemperatureValue.text = "$printOutputTemperatureHigh\u00B0"
            ConnectionStatus.text = "Temperature span from $printOutputTemperatureLow° to $printOutputTemperatureHigh°"
        }

        // Remember the last value
        printOutputTemperatureLast = temperatureInt
        return "$timeAndDate - $sensorNumber - $temperatureString \u00B0\n"
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
