package com.example.proyectodam.models

import android.media.MediaSession2Service
import java.io.Serializable

class Medicacion (
    var id: Long =0,
    var email: String = "",
    var nombre: String = "",
    var hora: String = "",
    var fecha: String = "",
    var descripcion: String = "",
    var cantidad: String = "",
    var fechaCreacion: Long = 0,
    var fechaModificacion: Long = 0
     ) : Serializable