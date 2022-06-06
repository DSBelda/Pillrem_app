package my.dsbelda.proyectodam.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.proyectodam.R
import my.dsbelda.proyectodam.activities.MainActivity
import my.dsbelda.proyectodam.database.DBMedicina
import my.dsbelda.proyectodam.models.Medicacion
import java.util.*


class MedicacionService  : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var tts: TextToSpeech? = null

    override fun onCreate() {
        super.onCreate()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val medicacionId = intent?.getLongExtra("medicacionId", 0)
        val db = DBMedicina(this)
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val medicacion = db.getMedicinaById(medicacionId ?: 0, email.toString())
        showAlarmNotification(medicacion)
        val current = resources.configuration.locale
        val speakText = medicacion.nombre + " " + medicacion.cantidad + " " + medicacion.descripcion
        tts = TextToSpeech(this,
            TextToSpeech.OnInitListener {
                if (it != TextToSpeech.ERROR) {
                    tts?.language = current
                    tts?.speak(speakText, TextToSpeech.QUEUE_ADD, null, null)
                } else {
                    Log.d("MedicacionService", "Error: $it")
                }
            })
        return START_STICKY
    }

    private fun showAlarmNotification(medicacion: Medicacion) {
        createNotificationChannel(medicacion.id.toInt())
        val textoNotificacion = medicacion.cantidad + " - " + medicacion.descripcion
        // build notification
        val builder = NotificationCompat.Builder(this, medicacion.id.toString())
            .setSmallIcon(R.drawable.ic_imagennotificacion) //set icon for notification
            .setContentTitle(medicacion.nombre) //set title of notification
            .setContentText(textoNotificacion)//this is notification message
            .setAutoCancel(true) // makes auto cancel of notification
            .setPriority(NotificationCompat.PRIORITY_HIGH) //set priority of notification

        val notificationIntent = Intent(applicationContext, MainActivity::class.java)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        //notification message will get at NotificationView
        notificationIntent.putExtra("medicacionId", medicacion.id)
        notificationIntent.putExtra("from", "Notification")

        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, notificationIntent,
            PendingIntent.FLAG_MUTABLE
        )
        builder.setContentIntent(pendingIntent)
        val notification = builder.build()

        // Add as notification
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(medicacion.id.toInt(), notification)
    }

    private fun createNotificationChannel(id: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                id.toString(),
                "Medicacion Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        }

        tts?.stop()
        tts?.shutdown()
    }

}