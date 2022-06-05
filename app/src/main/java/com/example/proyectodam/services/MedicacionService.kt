package com.example.proyectodam.services

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
import com.example.proyectodam.activities.MainActivity
import com.example.proyectodam.database.DBMedicina
import com.example.proyectodam.models.Medicacion
import java.util.*


class MedicacionService  : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var tts: TextToSpeech? = null

    override fun onCreate() {
        super.onCreate()
        Log.d("MedicacionService", "onCreate called")
        // enable to play a custom ring tone
        /*mediaPlayer = MediaPlayer.create(this, R.raw.alarm_ringtone)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()*/
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d("MedicacionService", "onBind called")
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MedicacionService", "onStartCommand called")
        val medicacionId = intent?.getLongExtra("medicacionId", 0)
        val db = DBMedicina(this)
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val medicacion = db.getMedicinaById(medicacionId ?: 0, email.toString())
        Log.d("prueba", "Medicacion enviada $medicacion")
        Log.d("prueba", "Medicacion enviada con ID ${medicacion.id}")
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
        Log.d("MedicacionService", speakText)
        return START_STICKY
    }

    private fun showAlarmNotification(medicacion: Medicacion) {
        Log.d("MedicacionService", "showAlarmNotification called")
        Log.d("prueba", "show Alarm llega  ${medicacion.id}")
        createNotificationChannel(medicacion.id.toInt())
        val textoNotificacion = medicacion.cantidad + " - " + medicacion.descripcion
        // build notification
        val builder = NotificationCompat.Builder(this, medicacion.id.toString())
            .setSmallIcon(R.drawable.ic_logo_medicina) //set icon for notification
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
        Log.d("MedicacionService", "onDestroy called")
        super.onDestroy()

        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        }

        tts?.stop()
        tts?.shutdown()
    }

}