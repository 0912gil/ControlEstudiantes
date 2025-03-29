package com.example.controlestudiantes

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.controlestudiantes.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializamos la autenticación de Firebase
        auth = FirebaseAuth.getInstance()

        // Verificamos si el usuario está autenticado
        if (auth.currentUser == null) {
            // Si el usuario no está autenticado, redirigimos al Login
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // Terminamos la actividad actual para que no se quede en la pila
        }

        // Configuración del BottomNavigationView
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_register_note -> {
                    // Redirigir a la actividad de registrar notas
                    val intent = Intent(this, RegisterNotesActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.menu_list_students -> {
                    // Redirigir a la actividad de listar estudiantes
                    val intent = Intent(this, ListStudentsActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }

            // Configurar el botón de cerrar sesión
            binding.btnLogout.setOnClickListener {
                // Cerrar sesión
                auth.signOut()

                // Redirigir al LoginActivity
                startActivity(Intent(this, LoginActivity::class.java))
                finish() // Terminamos la actividad principal para que no vuelva a ella después del cierre de sesión
            }
        }
    }