package my.dsbelda.proyectodam.utils

import android.content.Context
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

object Util {


    /**
     * Facilita usar los Toast
     */
    fun showToastMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Transforma de Date a String en el formato deseado
     */
    fun getFormattedDateInString(timeInMillis: Long, format: String): String {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        return sdf.format(timeInMillis)
    }

    /**
     * Formateo la hora
     */
    fun getFormattedDate(timeInString: String, format: String): Date {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        return sdf.parse(timeInString)
    }

}