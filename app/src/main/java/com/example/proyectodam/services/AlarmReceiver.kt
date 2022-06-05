package com.example.proyectodam.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {

        val medicacionId = intent?.getLongExtra("medicacionId", 0)
        val isServiceRunning = intent?.getBooleanExtra("isServiceRunning", false)

        val medicacionServiceIntent = Intent(context, MedicacionService::class.java)
        medicacionServiceIntent.putExtra("medicacionId", medicacionId)
        if (!isServiceRunning!!) {
            context.startService(medicacionServiceIntent)
        }
    }
}
