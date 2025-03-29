package com.example.controlestudiantes

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class ListStudentsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var studentList: ArrayList<String> // Lista para almacenar los nombres de los estudiantes
    private lateinit var studentIds: ArrayList<String> // Lista para almacenar los ids de los estudiantes
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_students)

        recyclerView = findViewById(R.id.recyclerViewStudents)
        studentList = ArrayList()
        studentIds = ArrayList()

        // Asegúrate de que la referencia a la base de datos esté bien
        database = FirebaseDatabase.getInstance().getReference("notas")

        // Recuperar estudiantes de Firebase
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                studentList.clear()
                studentIds.clear()

                // Verifica que tienes datos en Firebase
                if (snapshot.exists()) {
                    for (studentSnapshot in snapshot.children) {
                        val studentId = studentSnapshot.key
                        val finalGrade = studentSnapshot.child("finalGrade").getValue(Double::class.java)
                        val studentGrade = studentSnapshot.child("grade").getValue(String::class.java)
                        val studentLastName = studentSnapshot.child("lastName").getValue(String::class.java)
                        val studentName = studentSnapshot.child("name").getValue(String::class.java)
                        val studentSubject = studentSnapshot.child("subject").getValue(String::class.java)

                        // Verificar si los valores son nulos
                        if (studentName != null && studentLastName != null && studentId != null && studentGrade != null && finalGrade != null && studentSubject != null) {
                            val studentInfo = "$studentName $studentLastName - $studentGrade - Nota: $finalGrade - Asignatura: $studentSubject"
                            studentList.add(studentInfo)
                            studentIds.add(studentId)
                        }

                        // Agregar un log para verificar si los datos son correctos
                        Log.d("FirebaseData", "Estudiante: $studentName $studentLastName, ID: $studentId")
                    }

                    // Usamos RecyclerView con el adaptador personalizado
                    val adapter = StudentAdapter(this@ListStudentsActivity, studentList, studentIds)
                    recyclerView.layoutManager = LinearLayoutManager(this@ListStudentsActivity)
                    recyclerView.adapter = adapter
                } else {
                    // Si no hay datos, muestra un mensaje
                    Toast.makeText(this@ListStudentsActivity, "No hay estudiantes registrados.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ListStudentsActivity, "Error al cargar los estudiantes.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}