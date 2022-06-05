package com.example.proyectodam.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectodam.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_auth_register.*
import kotlinx.android.synthetic.main.activity_auth_register.bRegister
import kotlinx.android.synthetic.main.activity_auth_register.etRegisterPass

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth_register)

        //Setup
        setup()
    }

    private fun setup(){

        title = "Registro"

        bRegister.setOnClickListener {
            if(etRegisterMail.text.isNotEmpty() && etRegisterPass.text.isNotEmpty()
                && etRegisterUser.text.isNotEmpty()) {

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(etRegisterMail.text.toString(),
                etRegisterPass.text.toString()).addOnCompleteListener{

                    if(it.isSuccessful) {
                        showLogin(it.result?.user?.email ?: "", ProviderType.BASIC)
                    }else{
                        showAlert()
                    }
                }
            }
        }
    }




    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("ERROR")
        builder.setMessage("Se ha producido error al registrar el usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showLogin(email:String, provider: ProviderType) {
        val intent = Intent(this, AuthActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider)
        }
        startActivity(intent)
    }
}