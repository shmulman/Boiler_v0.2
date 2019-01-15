package il.co.shmulman.www.boiler_02

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_log.*

class ActivityLog : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        // Read SharedPreferences file from previous Log visit
        val sharedPreferenceVariableForLogFile = getSharedPreferences("myfile",MODE_PRIVATE)
        val errorCodeSet = setOf<String>()
        val dataFromSharedPreference = sharedPreferenceVariableForLogFile.getStringSet("KeySet", errorCodeSet)

        // Initiate ArrayList from ShearedPreferences
        var startDataArrayList = arrayListOf<String>()
        if (dataFromSharedPreference.isNotEmpty()){
            startDataArrayList = ArrayList(dataFromSharedPreference)
        }


        // This activity receives the data via intent and print it as Log
        val logDataArrayList : ArrayList<String> = intent.getStringArrayListExtra ("LogData")

        // Add previous data from previous Log
        logDataArrayList.addAll(startDataArrayList)

        if (logDataArrayList.isEmpty()) {
            LogOutput.append("No sensors data found")
        } else {
            for (element in logDataArrayList) {
                LogOutput.append(element)
            }
        }

        // Go back to Main Activity
        LogBackBtn.setOnClickListener {
            val intentBackToMain = Intent (applicationContext, MainActivity::class.java)
            startActivity(intentBackToMain)
        }

        //Add the received data via intent to the SharedPreference set of strings
        var dataFromSharedPreferenceMutableString : MutableSet<String>
        dataFromSharedPreferenceMutableString = logDataArrayList.toMutableSet()
        with(sharedPreferenceVariableForLogFile.edit()){
            putStringSet("KeySet",dataFromSharedPreferenceMutableString)
            apply()
        }

    }
}
