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
        val logDataArrayList : ArrayList<String> = intent.getStringArrayListExtra ("textLogData")
        if (logDataArrayList.isEmpty()) {
            LogOutput.append("No sensors data found")
        } else {
            var outputStringForLog = logDataArrayList.toString()
            outputStringForLog = outputStringForLog.replace("[", "")
            outputStringForLog = outputStringForLog.replace("]", "")
            outputStringForLog = outputStringForLog.replace(",", "")
            LogOutput.append(outputStringForLog)
        }

        // Go back to Main Activity
        LogBackBtn.setOnClickListener {
            val intentBackToMain = Intent (applicationContext, MainActivity::class.java)
            startActivity(intentBackToMain)
        }
    }
}
