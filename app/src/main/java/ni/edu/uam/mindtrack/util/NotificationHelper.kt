package ni.edu.uam.mindtrack.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class NotificationHelper(private val context: Context) {
    private val prefs = ni.edu.uam.mindtrack.data.OnboardingPreferences(context)

    init {
        createNotificationChannel()
    }

    private fun isUserEnabled(): Boolean {
        return runBlocking { prefs.notificationsEnabledFlow.first() }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "MindTrack Notifications"
            val descriptionText = "Notificaciones de simulaciones y logros"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showSimulationFinishedNotification(result: String) {
        if (!isUserEnabled()) return
        
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Simulación Finalizada")
            .setContentText("Has obtenido el perfil: $result")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(SIMULATION_ID, builder.build())
        }
    }

    fun showAchievementUnlockedNotification(achievementName: String) {
        if (!isUserEnabled()) return

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.star_on)
            .setContentTitle("¡Nuevo Logro Desbloqueado!")
            .setContentText("Has ganado: $achievementName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }

    companion object {
        private const val CHANNEL_ID = "mindtrack_notifications"
        private const val SIMULATION_ID = 1001
    }
}
