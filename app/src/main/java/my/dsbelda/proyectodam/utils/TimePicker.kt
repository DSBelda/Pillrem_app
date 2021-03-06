package my.dsbelda.proyectodam.utils

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class TimePicker(val listener: (hora: Int, minuto: Int) -> Unit) : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    /**
     * Crea el timepicker para elegir la hora
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val hora = c.get(Calendar.HOUR_OF_DAY)
        val minuto = c.get(Calendar.MINUTE)

        return TimePickerDialog(activity,this, hora, minuto, DateFormat.is24HourFormat(activity))
    }

    /**
     * Devuelve los valores
     */
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        listener(hourOfDay,minute)
    }
}