package my.dsbelda.proyectodam.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.proyectodam.R
import my.dsbelda.proyectodam.activities.AuthActivity
import my.dsbelda.proyectodam.activities.ProviderType
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_new_pass.*
import kotlinx.android.synthetic.main.fragment_new_pass.view.*

class RecuperarContraseñaFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView: View = inflater.inflate(R.layout.fragment_new_pass, container, false)

        rootView.fragmenCancel.setOnClickListener(){
            dismiss()
        }

        rootView.fragmentChange.setOnClickListener(){
            if(editTextTextEmailAddress.text.isNotEmpty()){
                resetPassword()
            }else{
                Toast.makeText(activity,"ERROR",Toast.LENGTH_SHORT).show();
                editTextTextEmailAddress.setText("${editTextTextEmailAddress.text} CAMPO VACIO")
            }
        }

        return rootView
    }

    private fun resetPassword(){
        FirebaseAuth.getInstance().sendPasswordResetEmail(editTextTextEmailAddress.text.toString()).addOnCompleteListener(){
            if (it.isSuccessful) {
                Toast.makeText(activity,"Successful!",Toast.LENGTH_SHORT).show();
                dismiss()
            } else {
                Toast.makeText(activity,"ERROR",Toast.LENGTH_SHORT).show();
                editTextTextEmailAddress.setText("${editTextTextEmailAddress.text} ERROR")
            }
        }
    }
}