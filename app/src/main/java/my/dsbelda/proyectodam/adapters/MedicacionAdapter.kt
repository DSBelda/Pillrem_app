package my.dsbelda.proyectodam.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectodam.R
import my.dsbelda.proyectodam.activities.MainActivity
import my.dsbelda.proyectodam.models.Medicacion
import my.dsbelda.proyectodam.utils.Util
import kotlinx.android.synthetic.main.item_medicacion.view.*

class MedicacionAdapter(private val itemClick: OnItemClickListener) :
    RecyclerView.Adapter<MedicacionAdapter.ViewHolder>() {

    var medicacionList = mutableListOf<Medicacion>()

    /**
     * Al iniciar establece la vista de los items para el adaptador
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflate =
            LayoutInflater.from(parent.context).inflate(R.layout.item_medicacion, parent, false)
        return ViewHolder(inflate)
    }

    /**
     * Cuenta el tamaño de la lista
     */
    override fun getItemCount(): Int {
        return medicacionList.size
    }

    /**
     * Reemplaza las posiciones ya usadas para no establacer mas posiciones
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(medicacionList[position], position)
    }

    /**
     * Permite crear la lista dinamica del recycler con el la vista del item.
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bindItems(medicacion: Medicacion, position: Int) {
            itemView.itemInfo.text = medicacion.cantidad + "-" + medicacion.descripcion
            val medicacionFecha =
                Util.getFormattedDate(medicacion.fecha + " " + medicacion.hora, "dd/MM/YYYY HH:mm")
            if (medicacionFecha.time < System.currentTimeMillis()) {
                itemView.itemNombre.text = medicacion.nombre
            } else {
                itemView.itemNombre.text = medicacion.nombre
                itemView.itemNombre.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                itemView.itemInfo.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                itemView.itemHora.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            }
            itemView.itemHora.text = medicacion.hora
            itemView.itemMore.setOnClickListener {
                itemClick.onItemClick(medicacion, itemView.itemMore, adapterPosition)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(
            medicacion: Medicacion,
            view: View,
            position: Int
        )
    }

}
