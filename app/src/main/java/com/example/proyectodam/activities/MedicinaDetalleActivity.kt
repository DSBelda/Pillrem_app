package com.example.proyectodam.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectodam.R
import com.example.proyectodam.models.Medicacion
import kotlinx.android.synthetic.main.activity_medicina.*

class MedicinaDetalleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medicina)

        val medicina = intent.getSerializableExtra("medicina") as Medicacion

        etNombreMedicinaDetalle.text = medicina.nombre

    }
}