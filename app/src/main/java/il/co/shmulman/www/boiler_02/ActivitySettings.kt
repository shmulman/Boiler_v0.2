package il.co.shmulman.www.boiler_02

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_settings.*


class ActivitySettings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Go back to Main Activity
        SettingsBackBtn.setOnClickListener {
            val intentBackToMainFromSettings = Intent (applicationContext, MainActivity::class.java)
            startActivity(intentBackToMainFromSettings)
        }

        TemperatureCold.text = "Cold 0° to $coldTemperatureMaxValue°"
        TemperatureOk.text = "Ok $okTemperatureMinValue° to $okTemperatureMaxValue°"
        TemperatureHot.text = "Hot $hotTemperatureMinValue° to 100°"
    }
}
