package my.dsbelda.proyectodam.activities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import my.dsbelda.proyectodam.models.Medicacion
import my.dsbelda.proyectodam.adapters.MedicacionAdapter
import com.example.proyectodam.R
import my.dsbelda.proyectodam.database.DBMedicina
import my.dsbelda.proyectodam.services.AlarmReceiver
import my.dsbelda.proyectodam.services.MedicacionService
import my.dsbelda.proyectodam.utils.Util
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Enumeramos los providers posibles de la aplicacion
 */
enum class ProviderType{
    BASIC,
    GOOGLE
}
class MainActivity : AppCompatActivity(), MedicacionAdapter.OnItemClickListener {

    private lateinit var db: DBMedicina
    private lateinit var adapter: MedicacionAdapter
    private var medicacionList = mutableListOf<Medicacion>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /**
         * Recibe mediante un intent el email y provider del registro
         */
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")

        //Setup
        setup()

        // GUARDADO DATOS LOGIN
        if (email != null) {
            Login(email, provider)
        }

        /**
         * Instanciamos la base de datos y el adapatdor para actualizar la lista
         */
        db = DBMedicina(this)
        adapter = MedicacionAdapter(this)
        recycler_view_medicinas.adapter = adapter

        getAllMedicinasDB()

        bMainAñadir.setOnClickListener {
            val intent = Intent(this, AddMedicinaActivity::class.java)
            startActivity(intent)
        }

        /**
         * Recibe la pulsacion de la notifiacion y muestra una alerta
         */
        val from = intent.getStringExtra("from")
        if (from == "Notification") {
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
            val email = prefs.getString("email", null)
            val medicinaId = intent.getLongExtra("medicacionId", 0)
            val medicinaById = db.getMedicinaById(medicinaId, email.toString())
            showMedicinaAlert(medicinaById)
        }

    }

    /**
     * Muestra la toolbar personalizada en la vista principal
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     *  listeners de los botones de la Tool Bar
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.itemBarLogout -> {
                val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
                prefs.clear()
                prefs.apply()

                FirebaseAuth.getInstance().signOut()
                onBackPressed()
            }
            R.id.itemBarInfoApp -> {
                val intent = Intent(this, InfoActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Da titulo a la vista y listener de añadir medicina
     */
    private fun setup() {
        title = "Lista de medicaciones"

        bMainAñadir.setOnClickListener(){
            showAñadirMedicina()
        }
    }

    /**
     * Guarda los datos del login en preferencias global
     */
    private fun Login(email: String, provider: String?) {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()
    }

    /**
     * Abre la vista de añadir o editar medicina
     */
    private fun showAñadirMedicina() {
        val intent = Intent(this, AddMedicinaActivity::class.java)
        startActivity(intent)
    }

    /**
     * Actualiza la recycler list recibiendo la mutable list con los datos cargados desde la BD
     */
    private fun updateList(finalList: MutableList<Medicacion>) {
        adapter.medicacionList = finalList
        adapter.notifyDataSetChanged()

        if (medicacionList.size > 0) {
            recycler_view_medicinas.visibility = View.VISIBLE
            lbMainTextoVacio.visibility = View.GONE
        } else {
            recycler_view_medicinas.visibility = View.GONE
            lbMainTextoVacio.visibility = View.VISIBLE
            lbMainTextoVacio.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35F)
        }
    }

    /**
     * Obtiene todas las medicinas ligadas al usuario desde la BD
     */
    private fun getAllMedicinasDB() {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        medicacionList = db.getAll(email.toString())
        updateList(medicacionList)
    }

    /**
     * Cuando incia la vista se ejecuta
     */
    override fun onResume() {
        super.onResume()
        getAllMedicinasDB()
    }

    /**
     * Abre un modal cuando pulsas la notifcacion de la alarma
     */
    private fun showMedicinaAlert(medicacion: Medicacion) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("ALARMA SONANDO!")
        builder.setMessage("Te has tomado la medicina?")
        builder.setPositiveButton(R.string.StopAlarm) { dialog, _ ->
            Util.showToastMessage(this, "Tu alarma se ha parado")
            dialog.dismiss()
            stopAlarm()
            stopMedicinaService()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    /**
     * Permite para la alarma activa, desactivandola del segundo plano
     */
    private fun stopAlarm() {
        val intent = Intent(this, AlarmReceiver::class.java)
        val sender = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(sender)
    }

    /**
     * Para el service en segundo plano
     */
    private fun stopMedicinaService() {
        val medicinaService = Intent(this, MedicacionService::class.java)
        stopService(medicinaService)
    }


    /**
     * Listeners de los botones de cada item
     */
    override fun onItemClick(
        medicina: Medicacion,
        view: View,
        position: Int
    ) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener {
            if (it.title == getString(R.string.updateMedicina)) {
                startActivity(
                    Intent(this, AddMedicinaActivity::class.java)
                        .putExtra("medicina", medicina)
                )
            } else if (it.title == getString(R.string.eliminarMedicinaDetalle)) {
                db.deleteMedicacionById(medicina.id)
                getAllMedicinasDB()
            }
            return@setOnMenuItemClickListener true
        }
        popupMenu.show()
    }

}