package com.example.controlestudiantes

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.controlestudiantes.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRegisterBinding  // ViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa ViewBinding
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Usa el binding para acceder a los elementos
        binding.btnRegister.setOnClickListener {
            val userEmail = binding.etEmail.text.toString()
            val userPassword = binding.etPassword.text.toString()

            // Crear usuario en Firebase Authentication
            auth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val database = FirebaseDatabase.getInstance().getReference("usuarios")
                        val userId = user?.uid

                        val userMap = hashMapOf(
                            "name" to binding.etName.text.toString(),
                            "email" to userEmail,
                            "password" to userPassword
                        )

                        userId?.let {
                            database.child(it).setValue(userMap)
                        }

                        // Navegar a la pantalla principal
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
