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


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferenceVariableForLogFile = getSharedPreferences("myfile",MODE_PRIVATE)
        sharedPreferenceVariableForLogFile.edit().clear()
        sharedPreferenceVariableForLogFile.edit().remove("KeySet")
        sharedPreferenceVariableForLogFile.edit().commit()

        ReadSensorsBtn.setOnClickListener() {
            ConnectionStatus.text = "Loading sensors ..."

            // Local axillary variables initiation
            var dataLoaded200 = false
            var dataLoaded201 = false
            var response200String = ""
            var response201String = ""

            // Read sensors data via HTML file thread
            doAsync{
                val response200 = URL("http://10.100.102.200/").readText()
                dataLoaded200 = true
                uiThread {
                    response200String = printOutput(response200,"200")

                    val sharedPreferenceVariableForLogFile = getSharedPreferences("myfile",MODE_PRIVATE)
                    // Add data to the Log database
                    //ConnectionStatus.text = response200String

                    val errorCodeSet = setOf<String>()
                    val dataFromSharedPreference = sharedPreferenceVariableForLogFile.getStringSet("KeySet", errorCodeSet)

                    dataFromSharedPreference.add(response200String)

                    with(sharedPreferenceVariableForLogFile.edit()){
                        //putStringSet("KeySet",mutableLogOfStrings)
                        putStringSet("KeySet",dataFromSharedPreference)
                        commit()
                    }
                }
                ConnectionStatus.text = printDataLoadedStatus(dataLoaded200,dataLoaded201) //<----------------------- GOOD

            }

            doAsync {
                val response201 = URL("http://10.100.102.201/").readText()
                dataLoaded201 = true
                uiThread {
                    response201String = printOutput(response201,"201")
                    val sharedPreferenceVariableForLogFile = getSharedPreferences("myfile",MODE_PRIVATE)
                    // Add data to the Log database
                    //ConnectionStatus.text = response201String

                    val errorCodeSet = setOf<String>()
                    val dataFromSharedPreference = sharedPreferenceVariableForLogFile.getStringSet("KeySet", errorCodeSet)

                    dataFromSharedPreference.add(response201String)

                    with(sharedPreferenceVariableForLogFile.edit()){
                        putStringSet("KeySet",dataFromSharedPreference)
                        commit()
                    }
                }
                ConnectionStatus.text = printDataLoadedStatus(dataLoaded200,dataLoaded201) //<----------------------- GOOD
            }

            // Print the result, whether the HTML file was read
            ConnectionStatus.text = printDataLoadedStatus(dataLoaded200,dataLoaded201) //<----------------------- GOOD

        }

        // Go to Log Activity and show the Log file
        ShowLogBtn.setOnClickListener {
            ConnectionStatus.text = "Read Log File"

            val intentToLog = Intent(applicationContext,ActivityLog::class.java)
            startActivity(intentToLog)
        }

        SettingsBtn.setOnClickListener{
            ConnectionStatus.text = "Go to Settings"

            val intentToSettings = Intent(applicationContext,ActivitySettings::class.java)
            startActivity(intentToSettings)
        }
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
