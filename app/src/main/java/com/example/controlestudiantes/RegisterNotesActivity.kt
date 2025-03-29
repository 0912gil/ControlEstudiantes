package com.example.controlestudiantes

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.controlestudiantes.databinding.ActivityRegisterNotesBinding
import com.google.firebase.database.FirebaseDatabase

class RegisterNotesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterNotesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar el binding
        binding = ActivityRegisterNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Datos para los Spinner (Grado y Materia)
        val grades = arrayOf("Primero", "Segundo", "Tercero", "Cuarto", "Quinto")
        val subjects = arrayOf("Matemáticas", "Lenguaje", "Ciencias", "Historia")

        // Adaptadores para los Spinner
        val gradeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, grades)
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGrade.adapter = gradeAdapter

        val subjectAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, subjects)
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSubject.adapter = subjectAdapter

        // Registrar la nota
        binding.btnRegisterNote.setOnClickListener {
            val name = binding.etName.text.toString()
            val lastName = binding.etLastName.text.toString()
            val grade = binding.spinnerGrade.selectedItem.toString()
            val subject = binding.spinnerSubject.selectedItem.toString()
            val finalGradeText = binding.etFinalGrade.text.toString()

            // Validaciones
            if (name.isEmpty() || lastName.isEmpty() || finalGradeText.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val finalGrade = finalGradeText.toDoubleOrNull()
            if (finalGrade == null || finalGrade < 0 || finalGrade > 10) {
                Toast.makeText(this, "La nota final debe ser un número entre 0 y 10.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Guardar la información en Firebase
            val database = FirebaseDatabase.getInstance().getReference("notas")
            val noteId = database.push().key // Generar un ID único para la nota

            val noteData = hashMapOf(
                "name" to name,
                "lastName" to lastName,
                "grade" to grade,
                "subject" to subject,
                "finalGrade" to finalGrade
            )

            noteId?.let {
                database.child(it).setValue(noteData)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Nota registrada con éxito.", Toast.LENGTH_SHORT).show()
                            finish() // Cerrar la actividad después de registrar la nota
                        } else {
                            Toast.makeText(this, "Error al registrar la nota.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}
