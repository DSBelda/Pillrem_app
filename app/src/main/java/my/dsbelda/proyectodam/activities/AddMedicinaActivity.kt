package my.dsbelda.proyectodam.activities

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectodam.R
import my.dsbelda.proyectodam.database.DBMedicina
import my.dsbelda.proyectodam.models.Medicacion
import my.dsbelda.proyectodam.services.AlarmReceiver
import my.dsbelda.proyectodam.services.MedicacionService
import my.dsbelda.proyectodam.utils.Util
import kotlinx.android.synthetic.main.activity_add_medicina.*
import kotlinx.coroutines.NonCancellable.cancel
import java.util.*


class AddMedicinaActivity : AppCompatActivity() {

    private lateinit var alarmManager: AlarmManager
    private lateinit var db: DBMedicina
    private val myCalendar = Calendar.getInstance()
    private var fecha: DatePickerDialog.OnDateSetListener? = null
    private var hora: Int = 0
    private var minuto: Int = 0
    private var medicinaGuardada = Medicacion()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_medicina)

        db = DBMedicina(this)

        if (intent.hasExtra("medicina")) {
            medicinaGuardada = intent.getSerializableExtra("medicina") as Medicacion
        }

        if (medicinaGuardada.id != 0L) {
            etAddNombre.setText(medicinaGuardada.nombre)
            etAddCantidad.setText(medicinaGuardada.cantidad)
            etAddDescripcion.setText(medicinaGuardada.descripcion)
            lbFechaMedicacion.text = medicinaGuardada.fecha
            lbHoraAddMedicacion.text = medicinaGuardada.hora
            val split = medicinaGuardada.fecha.split("/")
            val dia = split[0]
            val mes = split[1]
            val ano = split[2]
            val split1 = medicinaGuardada.hora.split(":")
            val hora = split1[0]
            val minuto = split1[1]
            myCalendar.set(Calendar.YEAR, ano.toInt())
            myCalendar.set(Calendar.MONTH, mes.toInt())
            myCalendar.set(Calendar.DAY_OF_MONTH, dia.toInt())
            myCalendar.set(Calendar.HOUR_OF_DAY, hora.toInt())
            myCalendar.set(Calendar.MINUTE, minuto.toInt())
            myCalendar.set(Calendar.SECOND, 0)
            bAddA??adirMedicamento.text = getString(R.string.updateMedicina)
        } else {
            updateDate()
            bAddA??adirMedicamento.text = getString(R.string.saveMedicina)
        }

        fecha = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDate()
        }
        bAddFechaMedicacion.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this, fecha, myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.datePicker.minDate = myCalendar.timeInMillis
            datePickerDialog.show()
        }

        bAddProgramarDosis.setOnClickListener {
            hora = myCalendar.get(Calendar.HOUR_OF_DAY)
            minuto = myCalendar.get(Calendar.MINUTE)

            val timePickerDialog =
                TimePickerDialog(
                    this,
                    TimePickerDialog.OnTimeSetListener(function = { _, hour, minute ->
                        myCalendar.set(Calendar.HOUR_OF_DAY, hour)
                        myCalendar.set(Calendar.MINUTE, minute)
                        myCalendar.set(Calendar.SECOND, 0)
                        updateTime(hour, minute)
                    }), hora, minuto, true
                )

            timePickerDialog.show()
        }

        /**
         * A??ade medaicmento con un on click listener
         */
        bAddA??adirMedicamento.setOnClickListener {
            if (etAddNombre.text?.isEmpty() == true) {
                Util.showToastMessage(this, "Porfavor introduce un nombre")
            } else if (lbHoraAddMedicacion.text == getString(R.string.hora)) {
                Util.showToastMessage(this, "Please selecciona la hora")
            } else {
                val nombre = etAddNombre.text.toString()
                val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
                val email = prefs.getString("email", null).toString()
                val cantidad = etAddCantidad.text.toString()
                val descripcion = etAddDescripcion.text.toString()
                val hora = lbHoraAddMedicacion.text.toString()
                val fecha = lbFechaMedicacion.text.toString()

                val medicacion = Medicacion()

                medicacion.nombre = nombre
                medicacion.email = email
                medicacion.cantidad = cantidad
                medicacion.descripcion = descripcion
                medicacion.hora = hora
                medicacion.fecha = fecha

                val guardarMedicacionId: Long
                guardarMedicacionId = if (medicinaGuardada.id != 0L) {
                    medicacion.id = medicinaGuardada.id
                    db.updateMedicacion(medicacion)
                    medicinaGuardada.id
                } else {
                    db.guardarMedicina(medicacion)
                }
                if (guardarMedicacionId != 0L) {
                    setMedicineAlarm(guardarMedicacionId)
                } else {
                    Util.showToastMessage(this, "Error guardando la medicina")
                }
            }
        }
    }

    /**
     * Actualiza la fecha
     */
    private fun updateDate() {
        val formattedDate = Util.getFormattedDateInString(myCalendar.timeInMillis, "dd/MM/YYYY")
        lbFechaMedicacion.text = formattedDate
    }

    /**
     * Actualiza la hora
     */
    @SuppressLint("SetTextI18n")
    private fun updateTime(hora: Int, minuto: Int) {
        this.hora = hora
        this.hora = minuto
        lbHoraAddMedicacion.text = "$hora:"+checkDigit(minuto)
    }

    /**
     * Establece la alarma recibiendo la id de la medicacion guardada
     */
    private fun setMedicineAlarm(guardarMedicacionId: Long) {
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val medicacionService = MedicacionService()
        val medicacionRecieverIntent = Intent(this, AlarmReceiver::class.java)

        medicacionRecieverIntent.putExtra("medicacionId", guardarMedicacionId)
        medicacionRecieverIntent.putExtra("isServiceRunning", isServiceRunning(medicacionService))
        val pendingIntent =
            PendingIntent.getBroadcast(this, guardarMedicacionId.toInt(), medicacionRecieverIntent, PendingIntent.FLAG_MUTABLE) //Pending.Intent.FLAG_MUTABLE corrige errores en android 12

        val formattedDate = Util.getFormattedDateInString(myCalendar.timeInMillis, "dd/MM/YYYY HH:mm")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, myCalendar.timeInMillis, pendingIntent
            )
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, myCalendar.timeInMillis, pendingIntent)
        }

        Util.showToastMessage(this, "Alarma configurada a las: $formattedDate")
        finish()
    }

    /**
     * Comprueba  que el Service esta ejecutandose en segundo plano
     */
    @Suppress("DEPRECATION")
    private fun isServiceRunning(medicacionService: MedicacionService): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (medicacionService.javaClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    /**
     * Se utiliza para darle formato a la fecha
     */
    fun checkDigit(number: Int): String? {
        return if (number <= 9) "0$number" else number.toString()
    }


}