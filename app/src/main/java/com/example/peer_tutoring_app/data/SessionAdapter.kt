package com.example.peer_tutoring_app.data


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.peer_tutoring_app.R
import com.example.peer_tutoring_app.model.TutorSession


class SessionAdapter(private val list: ArrayList<TutorSession>, private val context: Context) :
    RecyclerView.Adapter<SessionAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.card_layout, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(list[position])
    }


    override fun getItemCount(): Int {
        return list.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItem(session: TutorSession) {
            val tutorName: TextView = itemView.findViewById(R.id.tvTutorName)
            val subject: TextView = itemView.findViewById(R.id.tvSubject)
            val sessionTime: TextView = itemView.findViewById(R.id.tvSessionTime)


            tutorName.text = session.tutorName
            subject.text = session.subject
            sessionTime.text = session.sessionTime
        }
    }
}

