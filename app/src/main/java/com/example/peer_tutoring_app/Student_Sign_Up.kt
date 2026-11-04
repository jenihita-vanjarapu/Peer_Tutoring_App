package com.example.peer_tutoring_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.PopupWindow
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class Student_Sign_Up : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private val selectedElaClasses = ArrayList<String>()
    private val selectedMathClasses = ArrayList<String>()
    private val selectedSocialStudiesClasses = ArrayList<String>()
    private val selectedScienceClasses = ArrayList<String>()
    private val selectedDates = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_student_sign_up)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        val cbEla = findViewById<CheckBox>(R.id.Student_ELA_Selection)
        cbEla.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showstudent_ela_popup(cbEla)
            }
        }

        val cbMath = findViewById<CheckBox>(R.id.Student_Math_Selection)
        cbMath.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showstudent_math_popup(cbMath)
            }
        }

        val cbSocialStudies = findViewById<CheckBox>(R.id.Student_Social_Studies_Selection)
        cbSocialStudies.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showstudent_social_studies_popup(cbSocialStudies)
            }
        }

        val cbScience = findViewById<CheckBox>(R.id.Student_Science_Selection)
        cbScience.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showstudent_science_popup(cbScience)
            }
        }

        val btnChooseDate = findViewById<Button>(R.id.Student_SignUP_Choose_Btn)
        btnChooseDate.setOnClickListener {
            showstudent_date_popup(btnChooseDate)
        }

        val loginBtn = findViewById<Button>(R.id.Student_SignUP_Login_Btn)
        val emailEditText = findViewById<EditText>(R.id.Student_SignUp_Email)
        val passwordEditText = findViewById<EditText>(R.id.Student_SignUp_Password)
        val nameEditText = findViewById<EditText>(R.id.Student_SignUp_Name)

        loginBtn.setOnClickListener {
            val email = emailEditText.text.trim().toString()
            val password = passwordEditText.text.trim().toString()
            val name = nameEditText.text.trim().toString()

            if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty()){
                createStudent(email, password, name)
            } else {
                Snackbar.make(loginBtn, "Please fill in all fields", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun createStudent(email: String, password: String, name: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    saveStudentData(email, name)
                } else {
                    Snackbar.make(
                        findViewById(R.id.Student_SignUP_Login_Btn),
                        "Authentication failed: ${task.exception?.message}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun saveStudentData(email: String, name: String) {
        val student = hashMapOf(
            "Name" to name,
            "Email" to email,
            "ELA" to selectedElaClasses,
            "Math" to selectedMathClasses,
            "Social Studies" to selectedSocialStudiesClasses,
            "Science" to selectedScienceClasses,
            "Available Dates" to selectedDates
        )

        db.collection("Students")
            .document(email)
            .set(student)
            .addOnSuccessListener {
                Log.d("Student_Sign_Up", "DocumentSnapshot added with ID: $email")
                val intent = Intent(this, Student_Home_Page::class.java)
                intent.putExtra("USER_NAME", name)
                startActivity(intent)
                finish() 
            }
            .addOnFailureListener { e ->
                Log.w("Student_Sign_Up", "Error adding document", e)
                Snackbar.make(
                    findViewById(R.id.Student_SignUP_Login_Btn),
                    "Error saving profile: ${e.message}",
                    Snackbar.LENGTH_LONG
                ).show()
            }
    }

    private fun showstudent_ela_popup(mainCheckbox: CheckBox) {
        val view = LayoutInflater.from(this).inflate(R.layout.student_ela_popup, null)
        val popup = PopupWindow(
            view,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        val ch1 = view.findViewById<CheckBox>(R.id.cb_eng1)
        val ch2 = view.findViewById<CheckBox>(R.id.cb_ap_sem)
        val ch3 = view.findViewById<CheckBox>(R.id.cb_eng2)
        val ch4 = view.findViewById<CheckBox>(R.id.cb_ap_lang)
        val ch5 = view.findViewById<CheckBox>(R.id.cb_eng3)
        val ch6 = view.findViewById<CheckBox>(R.id.cb_ap_lit)
        val ch7 = view.findViewById<CheckBox>(R.id.cb_eng4)
        val ch8 = view.findViewById<CheckBox>(R.id.cb_creative)
        val allBoxes = listOf(ch1, ch2, ch3, ch4, ch5, ch6, ch7, ch8)

        allBoxes.forEach { it.isChecked = selectedElaClasses.contains(it.text.toString()) }

        view.findViewById<Button>(R.id.btnSaveEla).setOnClickListener {
            selectedElaClasses.clear()
            allBoxes.forEach { if (it.isChecked) selectedElaClasses.add(it.text.toString()) }
            popup.dismiss()
        }

        popup.setOnDismissListener { if (selectedElaClasses.isEmpty()) mainCheckbox.isChecked = false }
        popup.showAtLocation(mainCheckbox, Gravity.CENTER, 0, 0)
    }

    private fun showstudent_math_popup(mainCheckbox: CheckBox) {
        val view = LayoutInflater.from(this).inflate(R.layout.student_math_popup, null)
        val popup = PopupWindow(
            view,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        val ch1 = view.findViewById<CheckBox>(R.id.cb_algebra)
        val ch2 = view.findViewById<CheckBox>(R.id.cb_calculus)
        val ch3 = view.findViewById<CheckBox>(R.id.cb_geometry)
        val ch4 = view.findViewById<CheckBox>(R.id.cb_ap_calc_ab)
        val ch5 = view.findViewById<CheckBox>(R.id.cb_algebra2)
        val ch6 = view.findViewById<CheckBox>(R.id.cb_ap_calc_bc)
        val ch7 = view.findViewById<CheckBox>(R.id.cb_ap_precalc)
        val ch8 = view.findViewById<CheckBox>(R.id.cb_business_math)
        val allBoxes = listOf(ch1, ch2, ch3, ch4, ch5, ch6, ch7, ch8)

        allBoxes.forEach { it.isChecked = selectedMathClasses.contains(it.text.toString()) }

        view.findViewById<Button>(R.id.btnSaveMath).setOnClickListener {
            selectedMathClasses.clear()
            allBoxes.forEach { if (it.isChecked) selectedMathClasses.add(it.text.toString()) }
            popup.dismiss()
        }

        popup.setOnDismissListener { if (selectedMathClasses.isEmpty()) mainCheckbox.isChecked = false }
        popup.showAtLocation(mainCheckbox, Gravity.CENTER, 0, 0)
    }

    private fun showstudent_social_studies_popup(mainCheckbox: CheckBox) {
        val view = LayoutInflater.from(this).inflate(R.layout.student_social_studies_popup, null)
        val popup = PopupWindow(
            view,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        val ch1 = view.findViewById<CheckBox>(R.id.cb_world_history)
        val ch2 = view.findViewById<CheckBox>(R.id.cb_ap_human_geo)
        val ch3 = view.findViewById<CheckBox>(R.id.cb_us_history1)
        val ch4 = view.findViewById<CheckBox>(R.id.cb_ap_us_history)
        val ch5 = view.findViewById<CheckBox>(R.id.cb_us_history2)
        val ch6 = view.findViewById<CheckBox>(R.id.cb_ap_us_gov)
        val allBoxes = listOf(ch1, ch2, ch3, ch4, ch5, ch6)

        allBoxes.forEach { it.isChecked = selectedSocialStudiesClasses.contains(it.text.toString()) }

        view.findViewById<Button>(R.id.btnSaveSocialStudies).setOnClickListener {
            selectedSocialStudiesClasses.clear()
            allBoxes.forEach { if (it.isChecked) selectedSocialStudiesClasses.add(it.text.toString()) }
            popup.dismiss()
        }

        popup.setOnDismissListener { if (selectedSocialStudiesClasses.isEmpty()) mainCheckbox.isChecked = false }
        popup.showAtLocation(mainCheckbox, Gravity.CENTER, 0, 0)
    }

    private fun showstudent_science_popup(mainCheckbox: CheckBox) {
        val view = LayoutInflater.from(this).inflate(R.layout.student_science_popup, null)
        val popup = PopupWindow(
            view,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        val ch1 = view.findViewById<CheckBox>(R.id.cb_biology)
        val ch2 = view.findViewById<CheckBox>(R.id.cb_ap_physics2)
        val ch3 = view.findViewById<CheckBox>(R.id.cb_chemistry)
        val ch4 = view.findViewById<CheckBox>(R.id.cb_ap_physicsc)
        val ch5 = view.findViewById<CheckBox>(R.id.cb_physics)
        val ch6 = view.findViewById<CheckBox>(R.id.cb_ap_chemistry)
        val ch7 = view.findViewById<CheckBox>(R.id.cb_ap_physics1)
        val ch8 = view.findViewById<CheckBox>(R.id.cb_ap_biology)
        val ch9 = view.findViewById<CheckBox>(R.id.cb_ap_research)
        val ch10 = view.findViewById<CheckBox>(R.id.cb_ap_seminar_sci)
        val ch11 = view.findViewById<CheckBox>(R.id.cb_ap_psychology)
        val allBoxes = listOf(ch1, ch2, ch3, ch4, ch5, ch6, ch7, ch8, ch9, ch10, ch11)

        allBoxes.forEach { it.isChecked = selectedScienceClasses.contains(it.text.toString()) }

        view.findViewById<Button>(R.id.btnSaveScience).setOnClickListener {
            selectedScienceClasses.clear()
            allBoxes.forEach { if (it.isChecked) selectedScienceClasses.add(it.text.toString()) }
            popup.dismiss()
        }

        popup.setOnDismissListener { if (selectedScienceClasses.isEmpty()) mainCheckbox.isChecked = false }
        popup.showAtLocation(mainCheckbox, Gravity.CENTER, 0, 0)
    }

    private fun showstudent_date_popup(anchorView: android.view.View) {
        val view = LayoutInflater.from(this).inflate(R.layout.student_date_popup, null)
        val popup = PopupWindow(
            view,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        val ch1 = view.findViewById<CheckBox>(R.id.cb_tuesday)
        val ch2 = view.findViewById<CheckBox>(R.id.cb_wednesday)
        val ch3 = view.findViewById<CheckBox>(R.id.cb_thursday)
        val boxes = listOf(ch1, ch2, ch3)

        boxes.forEach { it.isChecked = selectedDates.contains(it.text.toString()) }

        view.findViewById<Button>(R.id.btnSaveDate).setOnClickListener {
            selectedDates.clear()
            boxes.forEach { if (it.isChecked) selectedDates.add(it.text.toString()) }
            popup.dismiss()
        }

        popup.showAtLocation(anchorView, Gravity.CENTER, 0, 0)
    }
}
