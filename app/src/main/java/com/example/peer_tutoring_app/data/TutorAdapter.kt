package com.example.peer_tutoring_app.data


import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.peer_tutoring_app.R
import com.example.peer_tutoring_app.model.Tutor


class TutorAdapter(private var tutorList: List<Tutor>) :
    RecyclerView.Adapter<TutorAdapter.TutorViewHolder>() {


    class TutorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivTutorImage: ImageView = itemView.findViewById(R.id.ivTutorImage)
        val tvTutorName: TextView = itemView.findViewById(R.id.tvTutorName)
        val tvTutordays: TextView = itemView.findViewById(R.id.tvTutordays)
        val tvTutorRecommendation: TextView = itemView.findViewById(R.id.tvTutorRecommendation)
        val btnInfo: Button = itemView.findViewById(R.id.pop_up_info)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TutorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tutor, parent, false)
        return TutorViewHolder(view)
    }


    private val avatarDrawables = listOf(
        R.drawable.images,
        R.drawable.images__2_,
        R.drawable.png_clipart_avatar_user_profile_icon_women_wear_frock_face_holidays_thumbnail,
        R.drawable.pngtree_cartoon_style_female_user_profile_icon_vector_illustraton_png_image_6489286,

    )

    override fun onBindViewHolder(holder: TutorViewHolder, position: Int) {
        val tutor = tutorList[position]
        holder.tvTutorName.text = tutor.name
        holder.tvTutordays.text = if (tutor.availableDates.isNotEmpty()) tutor.availableDates.joinToString(", ") else "No days"
        holder.tvTutorRecommendation.text = "Rec: ${tutor.recommendedTeacherName}"

        // Use tutor's specific image if available, otherwise pick a random avatar based on name or position
        if (tutor.imageResId != null) {
            holder.ivTutorImage.setImageResource(tutor.imageResId)
        } else {
            val randomAvatar = avatarDrawables[position % avatarDrawables.size]
            holder.ivTutorImage.setImageResource(randomAvatar)
        }

        holder.btnInfo.setOnClickListener {
            showTutorPopup(it, tutor)
        }
    }


    override fun getItemCount(): Int = tutorList.size

    fun updateList(newList: List<Tutor>) {
        tutorList = newList
        notifyDataSetChanged()
    }

    private fun showTutorPopup(anchorView: View, tutor: Tutor) {
        val context = anchorView.context
        val view = LayoutInflater.from(context).inflate(R.layout.tutor_selection_popup, null)
        val popup = PopupWindow(
            view,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        val tvName = view.findViewById<TextView>(R.id.tvPopupTutorName)
        val tvDays = view.findViewById<TextView>(R.id.tvPopupDays)
        val tvRecommendation = view.findViewById<TextView>(R.id.tvPopupRecommendation)
        val tvSubjects = view.findViewById<TextView>(R.id.tvPopupSubjects)
        val btnClose = view.findViewById<Button>(R.id.btnPopupClose)

        tvName.text = tutor.name
        tvDays.text = if (tutor.availableDates.isNotEmpty()) tutor.availableDates.joinToString(", ") else "No days available"
        
        val recommendationText = if (tutor.recommendedTeacherName.isNotEmpty()) {
            "${tutor.recommendedTeacherName}\n${tutor.recommendedTeacherEmail}"
        } else {
            "No recommendation provided"
        }
        tvRecommendation.text = recommendationText

        // Format subjects nicely as a bulleted or clean list
        val formattedSubjects = tutor.subjects.joinToString("\n") { "• $it" }
        tvSubjects.text = if (tutor.subjects.isNotEmpty()) formattedSubjects else "No subjects listed"

        btnClose.setOnClickListener {
            popup.dismiss()
        }

        popup.showAtLocation(anchorView.rootView, Gravity.CENTER, 0, 0)
    }
}

