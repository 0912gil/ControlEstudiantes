package com.example.controlestudiantes

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class EditStudentActivity : AppCompatActivity() {

    private lateinit var studentId: String
    private lateinit var etName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etFinalGrade: EditText
    private lateinit var spinnerGrade: Spinner
    private lateinit var spinnerSubject: Spinner
    private lateinit var database: DatabaseReference

    // Definir las opciones para los spinners
    private val gradeOptions = arrayOf("Primero", "Segundo", "Tercero", "Cuarto") // Ejemplo de grados
    private val subjectOptions = arrayOf("Lenguaje", "Matemáticas", "Historia", "Ciencias") // Ejemplo de asignaturas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_student)

        // Obtener el studentId que fue pasado desde ListStudentsActivity
        studentId = intent.getStringExtra("STUDENT_ID") ?: ""

        // Inicializar las vistas
        etName = findViewById(R.id.etName)
        etLastName = findViewById(R.id.etLastName)
        etFinalGrade = findViewById(R.id.etFinalGrade)
        spinnerGrade = findViewById(R.id.spinnerGrade)
        spinnerSubject = findViewById(R.id.spinnerSubject)

        // Configurar los adapters para los spinners
        val gradeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, gradeOptions)
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGrade.adapter = gradeAdapter

        val subjectAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, subjectOptions)
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSubject.adapter = subjectAdapter

        // Configurar la referencia de Firebase
        database = FirebaseDatabase.getInstance().getReference("notas")

        // Cargar los datos del estudiante
        loadStudentData(studentId)
        // Configurar el botón para guardar cambios
        findViewById<Button>(R.id.btnSave).setOnClickListener {
            saveChanges() // Llamamos al método para guardar cambios
        }
    }

    private fun loadStudentData(studentId: String) {
        database.child(studentId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val name = snapshot.child("name").getValue(String::class.java) ?: ""
                    val lastName = snapshot.child("lastName").getValue(String::class.java) ?: ""
                    val finalGrade = snapshot.child("finalGrade").getValue(Double::class.java) ?: 0.0
                    val grade = snapshot.child("grade").getValue(String::class.java) ?: ""
                    val subject = snapshot.child("subject").getValue(String::class.java) ?: ""

                    // Mostrar los datos en los campos correspondientes
                    etName.setText(name)
                    etLastName.setText(lastName)
                    etFinalGrade.setText(finalGrade.toString())

                    // Configura los spinners si el valor de grade y subject coinciden
                    val gradeIndex = gradeOptions.indexOf(grade)
                    val subjectIndex = subjectOptions.indexOf(subject)

                    // Si el valor está en la lista de opciones, selecciona el índice correspondiente
                    if (gradeIndex >= 0) {
                        spinnerGrade.setSelection(gradeIndex)
                    }
                    if (subjectIndex >= 0) {
                        spinnerSubject.setSelection(subjectIndex)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditStudentActivity, "Error al cargar los datos del estudiante.", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun saveChanges() {
        // Obtener los valores de los EditText y Spinner
        val name = etName.text.toString().trim()
        val lastName = etLastName.text.toString().trim()
        val finalGrade = etFinalGrade.text.toString().toDoubleOrNull() ?: 0.0
        val grade = spinnerGrade.selectedItem.toString()
        val subject = spinnerSubject.selectedItem.toString()

        // Verificar que los campos no estén vacíos
        if (name.isEmpty() || lastName.isEmpty() || finalGrade == 0.0) {
            Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear un mapa de los datos a actualizar
        val updatedData = hashMapOf<String, Any>(
            "name" to name,
            "lastName" to lastName,
            "finalGrade" to finalGrade,
            "grade" to grade,
            "subject" to subject
        )

        // Actualizar los datos en Firebase
        database.child(studentId).updateChildren(updatedData).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
                finish() // Volver a la actividad anterior
            } else {
                Toast.makeText(this, "Error al guardar los cambios.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
