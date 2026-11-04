package com.example.peer_tutoring_app.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.peer_tutoring_app.R
import com.example.peer_tutoring_app.model.StudentSession

class StudentSessionAdapter(private val sessionList: List<StudentSession>) :
    RecyclerView.Adapter<StudentSessionAdapter.SessionViewHolder>() {

    class SessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivStudentImage: ImageView = itemView.findViewById(R.id.ivStudentImage)
        val tvStudentName: TextView = itemView.findViewById(R.id.tvStudentName)
        val tvCourse: TextView = itemView.findViewById(R.id.tvCourse)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tutor_session, parent, false)
        return SessionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        val session = sessionList[position]
        
        holder.tvStudentName.text = session.studentName
        holder.tvCourse.text = "Course: ${session.course}"
        holder.tvTime.text = "Time: ${session.time}"

        // If an image is provided, set it; otherwise, keep the default from XML
        session.imageResId?.let {
            holder.ivStudentImage.setImageResource(it)
        }
    }

    override fun getItemCount(): Int = sessionList.size
}
