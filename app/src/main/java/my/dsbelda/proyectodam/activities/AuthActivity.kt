package my.dsbelda.proyectodam.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.proyectodam.R
import my.dsbelda.proyectodam.fragments.RecuperarContraseñaFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.activity_auth.bRegister
import kotlinx.android.synthetic.main.activity_auth.etLoginMail

class AuthActivity : AppCompatActivity() {

    private val GOOGLE_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        //Splash
        Thread.sleep(2000);
        setTheme(R.style.Theme_ProyectoDAM)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        //Setup
        setup()
        session()
    }

    /**
     * Si no te has logueado se hace visible la vista
     */
    override fun onStart() {
        super.onStart()
        linearLayoutAuth.visibility = View.VISIBLE
    }

    /**
     * Guarda en las preferencias globales en login
     */
    private fun session() {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val provider = prefs.getString("provider", null)
        if(email != null && provider != null) {
            linearLayoutAuth.visibility = View.INVISIBLE
            showMain(email, ProviderType.valueOf(provider))
        }
    }

    /**
     * Establece los parametros iniciales de la vista y los listeners correspondientes
     */
    private fun setup() {
        title = "Autenticacion"

        /**
         * Realiza un intent a la vista de el registro
         */
        bRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        /**
         * Comprueba los datos y realiza el login si es correcto
         */
        bLogin.setOnClickListener {
            if (etLoginMail.text.isNotEmpty() && etLoginPass.text.isNotEmpty()) {

                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    etLoginMail.text.toString(),
                    etLoginPass.text.toString()
                ).addOnCompleteListener {

                    if (it.isSuccessful) {
                        showMain(it.result?.user?.email ?: "", ProviderType.BASIC)
                    } else {
                        showAlert()
                    }
                }
            }
        }

        /**
         * Realiza el login mediante la cuenta de google de tu dispositivo
         */
        bLoginGoogle.setOnClickListener {

            //Configuración google

            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()

            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)

        }

        /**
         * Abre el modal para enviar un correo de recuperacion de contraseña
         */
        bRecuperarPass.setOnClickListener(){
            var dialog = RecuperarContraseñaFragment()

            dialog.show(supportFragmentManager, "RecuperarContraseña")

        }
    }

    /**
     * Funcion que permite ir a la vista principal
     */
    private fun showMain(email: String, provider: ProviderType) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(intent)
    }

    /**
     * Funcion que muestra una alerta de error
     */
    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("ERROR")
        builder.setMessage("Se ha producido error al registrar el usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    /**
     * Se ejecuta para ejecutar las tareas correspondientes al login de Google
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)

                if (account != null) {

                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {

                        if (it.isSuccessful) {
                            showMain(account.email ?: "", ProviderType.GOOGLE)
                        } else {
                            showAlert()
                        }
                    }
                }
            }catch (e: ApiException) {
                showAlert()
            }

        }
    }
}
