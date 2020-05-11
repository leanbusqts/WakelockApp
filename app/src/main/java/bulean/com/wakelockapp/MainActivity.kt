package bulean.com.wakelockapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val txtView: TextView = findViewById(R.id.txtView)

        // Turn on Wakelock
        val btnStart: Button = findViewById(R.id.start_service)
        btnStart.setOnClickListener(View.OnClickListener {
            FService.strtService(this)
            txtView.text = getString(R.string.wakelock_on)
        })
        // Turn off Wakelock
        val btnStop: Button = findViewById(R.id.stop_service)
        btnStop.setOnClickListener(View.OnClickListener {
            FService.stpService(this)
            txtView.text = getString(R.string.wakelock_off)
        })
    }
}