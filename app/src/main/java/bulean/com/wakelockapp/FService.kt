package bulean.com.wakelockapp

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat

class FService : Service() {

    companion object {
        const val TAG = "FOREGROUND_SERVICE"
        const val WAKELOCK_TAG: String = "WK::WakelockTag"
        private const val CHANNEL_ID: String = "1234"
        private const val CHANNEL_NAME: String = "CHANNEL"
        private const val SERVICE_ID: Int = 1

        var IS_RUNNING: Boolean = false

        fun strtService(context: Context) {
            val startIntent = Intent(context, FService::class.java)
            context.startForegroundService(startIntent)
        }
        fun stpService(context: Context){
            val stopIntent = Intent(context, FService::class.java)
            context.stopService(stopIntent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        startForeService()
        acquireWakelock()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "ON START COMMAND")
        return START_STICKY
    }

    // Start ForegroundService
    private fun startForeService() {
        createNotificationChannel()
        startFS()
    }

    // Create Notification Channel
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT)

            nChannel.lightColor = Color.BLUE
            nChannel.lockscreenVisibility = NotificationCompat.VISIBILITY_PRIVATE
            val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(nChannel)
        }
    }

    private fun startFS() {
        val description = getString(R.string.description_notification)
        val title = String.format(getString(R.string.title_notification), getString(R.string.app_name))
        // Start ForegroundService
        startForeground(SERVICE_ID, getStickyNotification(title, description))
        IS_RUNNING = true
    }

    // Create Notification
    private fun getStickyNotification(title: String, message: String): Notification? {
        // Create an explicit intent for an Activity in your app
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)
        // Create notification builder.
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
        builder.setContentTitle(title)
        builder.setContentText(message)
        builder.setSmallIcon(R.drawable.ic_android)
        builder.priority = NotificationCompat.PRIORITY_DEFAULT
        // Make head-up notification.
        builder.setFullScreenIntent(pendingIntent, true)
        // Notification's tap action
        builder.setContentIntent(pendingIntent)
        // Build the notification.
        return builder.build()
    }

    override fun onDestroy() {
        releaseWakelock()
        IS_RUNNING = false
    }

    /* Wakelock
    * https://developer.android.com/reference/kotlin/android/os/PowerManager
    * https://developer.android.com/reference/kotlin/android/os/PowerManager.WakeLock
    * https://developer.android.com/training/scheduling/wakelock
    * */
    private var wk: PowerManager.WakeLock? = null
    private fun acquireWakelock() {
        releaseWakelock()
        val wakelock: PowerManager.WakeLock =
            (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK or
                        PowerManager.ACQUIRE_CAUSES_WAKEUP,
                    WAKELOCK_TAG).apply {
                    acquire()
                }
            }
        wk = wakelock
    }
    private fun releaseWakelock() {
        if(wk == null || !wk!!.isHeld)
            return
        wk?.release()
        wk = null
    }
}