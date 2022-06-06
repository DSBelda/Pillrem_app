package com.dsbelda.proyectodam.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
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

        title = "Sign Up"

        bRegister.setOnClickListener {
            if(etRegisterMail.text.isNotEmpty() && etRegisterPass.text.isNotEmpty()
                && etRegisterPass2.text.isNotEmpty()) {
                if(isSameKey()){
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(etRegisterMail.text.toString(),
                        etRegisterPass.text.toString()).addOnCompleteListener{

                        if(it.isSuccessful) {
                            showLogin(it.result?.user?.email ?: "", ProviderType.BASIC)
                        }else{
                            showAlert()
                        }
                    }
                }
            }else{
                Toast.makeText(this, "Campos vacios", LENGTH_SHORT).show()
            }
        }
    }

    private fun isSameKey(): Boolean {
        var samekeyok = false
       if (etRegisterPass.text.toString() == etRegisterPass2.text.toString()) {
             samekeyok = true
     } else {
            samekeyok = false
            Toast.makeText(this, "Pass doesn't match", Toast.LENGTH_SHORT).show()
        }


        return samekeyok
    }


    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("ERROR")
        builder.setMessage("Usuario ya existe")
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