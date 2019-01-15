package il.co.shmulman.www.boiler_02

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_log.*

class ActivityLog : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        // Read the Log data from the Main Activity
        //val sharedPreferenceVariableForLogFile = getSharedPreferences("myfile",MODE_PRIVATE)
        //val errorCodeSet = setOf<String>()
        //val dataFromSharedPreference = sharedPreferenceVariableForLogFile.getStringSet("KeySet", errorCodeSet)



        //The intent sends dataForLogArrayList and ActivityLog saves it to sharedPreference
        val logDataArrayList : ArrayList<String> = intent.getStringArrayListExtra ("LogData")

        if (logDataArrayList.isEmpty()) {
            LogOutput.append("No sensors data found")
        } else {
            var outputStringForLog = logDataArrayList.sorted().toString()
            //outputStringForLog = outputStringForLog.filterNot { it == "" }
            outputStringForLog = outputStringForLog.replace("[", "\u0000")
            outputStringForLog = outputStringForLog.replace("]", "\u0000")
            outputStringForLog = outputStringForLog.replace(',', '\u0000')
            outputStringForLog = outputStringForLog.filterNot{ it.equals("") }
            LogOutput.append(outputStringForLog)
        }

        // Go back to Main Activity
        LogBackBtn.setOnClickListener {
            val intentBackToMain = Intent (applicationContext, MainActivity::class.java)
            startActivity(intentBackToMain)
        }


        /*
        // ERROR
        // Add data to the Log database
        val sharedPreferenceVariableForLogFile = getSharedPreferences("myfile",MODE_PRIVATE)
        val errorCodeSet = setOf<String>()

        var dataFromSharedPreference = sharedPreferenceVariableForLogFile.getStringSet("KeySet", errorCodeSet)
        var dataFromSharedPreferenceMutableString : MutableSet<String> = dataFromSharedPreference
        //dataFromSharedPreferenceMutableString = dataFromSharedPreference

        dataFromSharedPreferenceMutableString.add(response201String) // operation is not supported for read-only collection kotlin
        with(sharedPreferenceVariableForLogFile.edit()){
            putStringSet("KeySet",dataFromSharedPreferenceMutableString)
            commit()
        }

        */
    }
}
