package com.example.controlestudiantes

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

class StudentAdapter(
    private val context: Context,
    private val studentList: ArrayList<String>,
    private val studentIds: ArrayList<String>
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val studentName = studentList[position]
        val studentId = studentIds[position]

        // Mostrar el nombre del estudiante
        holder.tvStudentName.text = studentName
// Agrega un listener para manejar el clic en cada ítem
        holder.itemView.setOnClickListener {
            // Obtener el studentId correspondiente a este estudiante
            val studentId = studentIds[position]

            // Crear un Intent para ir a EditStudentActivity
            val intent = Intent(context, EditStudentActivity::class.java)

            // Pasar el studentId a la actividad de edición
            intent.putExtra("STUDENT_ID", studentId)

            // Iniciar la actividad
            context.startActivity(intent)
        }
        // Acción de eliminar
        holder.btnDelete.setOnClickListener {
            // Mostrar un diálogo de confirmación
            AlertDialog.Builder(context)
                .setMessage("¿Estás seguro de que quieres eliminar este estudiante?")
                .setPositiveButton("Sí") { _, _ ->
                    // Eliminar el estudiante de Firebase
                    val database = FirebaseDatabase.getInstance().getReference("notas")
                    database.child(studentId).removeValue().addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(context, "Estudiante eliminado correctamente", Toast.LENGTH_SHORT).show()
                            // Actualizar la lista local después de eliminar el estudiante
                            studentList.removeAt(position)
                            studentIds.removeAt(position)
                            notifyItemRemoved(position)
                        } else {
                            Toast.makeText(context, "Error al eliminar el estudiante", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    override fun getItemCount(): Int = studentList.size

    // ViewHolder para cada item de la lista
    class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvStudentName: TextView = itemView.findViewById(R.id.tvStudentInfo)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)
    }
}
