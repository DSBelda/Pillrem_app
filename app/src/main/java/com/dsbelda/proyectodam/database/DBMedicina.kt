package com.dsbelda.proyectodam.database

import android.content.ContentValues
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.Settings.Global.getString
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.example.proyectodam.R
import com.dsbelda.proyectodam.activities.MainActivity
import com.dsbelda.proyectodam.models.Medicacion


class DBMedicina(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "PillRemDB"
        private const val TABLE_MEDICACIONES = "Medicaciones"
        private const val ID = "id"
        private const val EMAIL = "email"
        private const val NOMBRE = "nombre"
        private const val CANTIDAD = "cantidad"
        private const val DESCRIPCION = "descripcion"
        private const val HORA = "hora"
        private const val FECHA = "tiempo"
        private const val FECHA_CREACION = "fechaCreacion"
        private const val FECHA_MODIFICACION = "fechaModificacion"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE " + TABLE_MEDICACIONES + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + EMAIL + " TEXT,"
                + NOMBRE + " TEXT,"
                + CANTIDAD + " TEXT,"
                + DESCRIPCION + " TEXT,"
                + HORA + " TEXT,"
                + FECHA + " TEXT,"
                + FECHA_CREACION + " TEXT,"
                + FECHA_MODIFICACION + " TEXT " + ")")
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_MEDICACIONES")
        onCreate(db)
    }

    fun guardarMedicina(medicacion: Medicacion): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(NOMBRE, medicacion.nombre)
        contentValues.put(EMAIL, medicacion.email)
        contentValues.put(CANTIDAD, medicacion.cantidad)
        contentValues.put(DESCRIPCION, medicacion.descripcion)
        contentValues.put(HORA, medicacion.hora)
        contentValues.put(FECHA, medicacion.fecha)
        contentValues.put(FECHA_CREACION, System.currentTimeMillis())
        contentValues.put(FECHA_MODIFICACION, System.currentTimeMillis())
        // Inserting Row
        val success = db.insert(TABLE_MEDICACIONES, null, contentValues)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }

    fun getMedicinaById(medicacionId: Long, emailPref: String): Medicacion {
        val medicacion = Medicacion()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_MEDICACIONES WHERE $ID = '$medicacionId' AND $EMAIL = '$emailPref'"
        val cursor = db.rawQuery(query, null)
        if (cursor.count < 1) {
            cursor.close()
            return medicacion
        } else {
            cursor.moveToFirst()

            val id = cursor.getString(0).toLong()
            val email = cursor.getString(1)
            val nombre = cursor.getString(2)
            val cantidad = cursor.getString(3)
            val descripcion = cursor.getString(4)
            val hora = cursor.getString(5)
            val fecha = cursor.getString(6)
            val fechaCreacion = cursor.getLong(7)
            val fechaModificacion = cursor.getLong(8)

            medicacion.id = id
            medicacion.email = email
            medicacion.nombre = nombre
            medicacion.cantidad = cantidad
            medicacion.descripcion = descripcion
            medicacion.hora = hora
            medicacion.fecha = fecha
            medicacion.fechaCreacion = fechaCreacion
            medicacion.fechaModificacion = fechaModificacion
        }
        cursor.close()
        db.close()
        return medicacion
    }

    //method to update data
    fun updateMedicacion(medicacion: Medicacion): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(NOMBRE, medicacion.nombre)
        contentValues.put(CANTIDAD, medicacion.cantidad)
        contentValues.put(DESCRIPCION, medicacion.descripcion)
        contentValues.put(HORA, medicacion.hora)
        contentValues.put(FECHA, medicacion.fecha)
        contentValues.put(FECHA_CREACION, medicacion.fechaCreacion)
        contentValues.put(FECHA_MODIFICACION, System.currentTimeMillis())

        // Updating Row
        val success = db.update(TABLE_MEDICACIONES, contentValues, "$ID=" + medicacion.id, null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return success
    }

    //method to delete data
    fun deleteMedicacionById(id: Long): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ID, id) // EmpModelClass UserId
        // Deleting Row
        val rowId = db.delete(TABLE_MEDICACIONES, "$ID=$id", null)
        //2nd argument is String containing nullColumnHack
        db.close() // Closing database connection
        return rowId
    }

    //method to delete data
    fun deleteAll(emailPref: String){
        val db = this.readableDatabase
        val sql = ("DELETE * from $TABLE_MEDICACIONES WHERE $EMAIL = " + emailPref + " ORDER BY $FECHA_MODIFICACION DESC")
        db?.execSQL(sql)
    }

    fun getAll(emailPref: String): MutableList<Medicacion> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("select * from $TABLE_MEDICACIONES WHERE $EMAIL = '$emailPref' ORDER BY $FECHA_MODIFICACION DESC", null)
        val medicacionList = mutableListOf<Medicacion>()

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast) {
                val medicacion = Medicacion()
                val id = cursor.getString(0).toLong()
                val email = cursor.getString(1)
                val nombre = cursor.getString(2)
                val cantidad = cursor.getString(3)
                val descripcion = cursor.getString(4)
                val hora = cursor.getString(5)
                val fecha = cursor.getString(6)
                val fechaCreacion = cursor.getLong(7)
                val fechaModificacion = cursor.getLong(8)

                medicacion.id = id
                medicacion.email = email
                medicacion.nombre = nombre
                medicacion.cantidad = cantidad
                medicacion.descripcion = descripcion
                medicacion.hora = hora
                medicacion.fecha = fecha
                medicacion.fechaCreacion = fechaCreacion
                medicacion.fechaModificacion = fechaModificacion

                medicacionList.add(medicacion)
                cursor.moveToNext()
            }
        }
        cursor.close()
        db.close()
        return medicacionList
    }


}