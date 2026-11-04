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

class Tutor_Sign_Up : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore
    private val selectedEla = ArrayList<String>()
    private val selectedMath = ArrayList<String>()
    private val selectedSocialStudies = ArrayList<String>()
    private val selectedScience = ArrayList<String>()
    private val selectedDates = ArrayList<String>()
    private var recommendedTeacherName: String = ""
    private var recommendedTeacherEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tutor_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val cbEla = findViewById<CheckBox>(R.id.Tutor_ELA_Selection)
        val cbMath = findViewById<CheckBox>(R.id.Tutor_Math_Selection)
        val cbSocialStudies = findViewById<CheckBox>(R.id.Tutor_Social_Studies_Selection)
        val cbScience = findViewById<CheckBox>(R.id.Tutor_Science_Selection)
        val btnChooseDate = findViewById<Button>(R.id.Tutor_SignUP_Choose_Btn)
        val btnRecommendation = findViewById<Button>(R.id.btnRecommendation)

        cbEla.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showtutor_ela_popup(cbEla)
            }
        }

        cbMath.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showtutor_math_popup(cbMath)
            }
        }

        cbSocialStudies.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showtutor_social_studies_popup(cbSocialStudies)
            }
        }

        cbScience.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showtutor_science_popup(cbScience)
            }
        }

        btnChooseDate.setOnClickListener {
            showtutor_date_popup(btnChooseDate)
        }

        btnRecommendation.setOnClickListener {
            showtutor_recommendation_popup(btnRecommendation)
        }

        auth = FirebaseAuth.getInstance()
        val loginBtn = findViewById<Button>(R.id.Tutor_SignUP_Login_Btn)
        val emailEditText = findViewById<EditText>(R.id.Tutor_SignUp_Email)
        val passwordEditText = findViewById<EditText>(R.id.Tutor_SignUp_Password)
        val nameEditText = findViewById<EditText>(R.id.Tutor_SignUp_Name)

        loginBtn.setOnClickListener() {
            val email = emailEditText.text.trim().toString()
            val password = passwordEditText.text.trim().toString()
            val name = nameEditText.text.trim().toString()

            if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty()){
                createUser(email, password, name)
            } else {
                Snackbar.make(loginBtn, "Please fill in all fields", Snackbar.LENGTH_LONG).show()
            }
        }

    }

    fun createUser(email:String, password:String, name: String){
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful){
                    saveTutorData(email, name)
                }
                else{
                    Snackbar.make(
                        findViewById(R.id.Tutor_SignUP_Login_Btn),
                        "Authentication failed: ${task.exception?.message}",
                            Snackbar.LENGTH_LONG).show()

                }
            }
    }

    private fun saveTutorData(email: String, name: String) {
        val tutor = hashMapOf(
            "Name" to name,
            "Email" to email,
            "ELA" to selectedEla,
            "Math" to selectedMath,
            "Social Studies" to selectedSocialStudies,
            "Science" to selectedScience,
            "Available Dates" to selectedDates,
            "Recommended Teacher Name" to recommendedTeacherName,
            "Recommended Teacher Email" to recommendedTeacherEmail
        )

        db.collection("Tutors")
            .document(email)
            .set(tutor)
            .addOnSuccessListener {
                Log.d("Tutor_Sign_Up", "DocumentSnapshot added with ID: $email")
                val intent = Intent(this, Tutor_Homepage::class.java)
                intent.putExtra("USER_NAME", name)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Log.w("Tutor_Sign_Up", "Error adding document", e)
                Snackbar.make(
                    findViewById(R.id.Tutor_SignUP_Login_Btn),
                    "Error saving profile: ${e.message}",
                    Snackbar.LENGTH_LONG).show()
            }
    }


    private fun showtutor_ela_popup(mainCheckbox: CheckBox) {
        val view = LayoutInflater.from(this).inflate(R.layout.tutor_ela_popup, null)
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

        allBoxes.forEach { it.isChecked = selectedEla.contains(it.text.toString()) }

        view.findViewById<Button>(R.id.btnSaveEla).setOnClickListener {
            selectedEla.clear()
            allBoxes.forEach { if (it.isChecked) selectedEla.add(it.text.toString()) }
            popup.dismiss()
        }

        popup.setOnDismissListener { if (selectedEla.isEmpty()) mainCheckbox.isChecked = false }
        popup.showAtLocation(mainCheckbox, Gravity.CENTER, 0, 0)
    }

    private fun showtutor_math_popup(mainCheckbox: CheckBox) {
        val view = LayoutInflater.from(this).inflate(R.layout.tutor_math_popup, null)
        val popup = PopupWindow(
            view,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        val boxes = listOf(
            view.findViewById<CheckBox>(R.id.cb_algebra),
            view.findViewById<CheckBox>(R.id.cb_calculus),
            view.findViewById<CheckBox>(R.id.cb_geometry),
            view.findViewById<CheckBox>(R.id.cb_ap_calc_ab),
            view.findViewById<CheckBox>(R.id.cb_algebra2),
            view.findViewById<CheckBox>(R.id.cb_ap_calc_bc),
            view.findViewById<CheckBox>(R.id.cb_ap_precalc),
            view.findViewById<CheckBox>(R.id.cb_business_math)
        )

        boxes.forEach { it.isChecked = selectedMath.contains(it.text.toString()) }

        view.findViewById<Button>(R.id.btnSaveMath).setOnClickListener {
            selectedMath.clear()
            boxes.forEach { if (it.isChecked) selectedMath.add(it.text.toString()) }
            popup.dismiss()
        }

        popup.setOnDismissListener { if (selectedMath.isEmpty()) mainCheckbox.isChecked = false }
        popup.showAtLocation(mainCheckbox, Gravity.CENTER, 0, 0)
    }

    private fun showtutor_social_studies_popup(mainCheckbox: CheckBox) {
        val view = LayoutInflater.from(this).inflate(R.layout.tutor_social_studies_popup, null)
        val popup = PopupWindow(
            view,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        val boxes = listOf(
            view.findViewById<CheckBox>(R.id.cb_world_hist),
            view.findViewById<CheckBox>(R.id.cb_ap_human_geo),
            view.findViewById<CheckBox>(R.id.cb_us_hist1),
            view.findViewById<CheckBox>(R.id.cb_ap_us_hist),
            view.findViewById<CheckBox>(R.id.cb_us_hist2),
            view.findViewById<CheckBox>(R.id.cb_ap_us_gov)
        )

        boxes.forEach { it.isChecked = selectedSocialStudies.contains(it.text.toString()) }

        view.findViewById<Button>(R.id.btnSaveSocialStudies).setOnClickListener {
            selectedSocialStudies.clear()
            boxes.forEach { if (it.isChecked) selectedSocialStudies.add(it.text.toString()) }
            popup.dismiss()
        }

        popup.setOnDismissListener { if (selectedSocialStudies.isEmpty()) mainCheckbox.isChecked = false }
        popup.showAtLocation(mainCheckbox, Gravity.CENTER, 0, 0)
    }

    private fun showtutor_science_popup(mainCheckbox: CheckBox) {
        val view = LayoutInflater.from(this).inflate(R.layout.tutor_science_popup, null)
        val popup = PopupWindow(
            view,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        val boxes = listOf(
            view.findViewById<CheckBox>(R.id.cb_biology),
            view.findViewById<CheckBox>(R.id.cb_ap_physics2),
            view.findViewById<CheckBox>(R.id.cb_chemistry),
            view.findViewById<CheckBox>(R.id.cb_ap_physics_c),
            view.findViewById<CheckBox>(R.id.cb_physics),
            view.findViewById<CheckBox>(R.id.cb_ap_chem),
            view.findViewById<CheckBox>(R.id.cb_ap_physics1),
            view.findViewById<CheckBox>(R.id.cb_ap_bio),
            view.findViewById<CheckBox>(R.id.cb_ap_research),
            view.findViewById<CheckBox>(R.id.cb_ap_sem_science),
            view.findViewById<CheckBox>(R.id.cb_ap_psych)
        )

        boxes.forEach { it.isChecked = selectedScience.contains(it.text.toString()) }

        view.findViewById<Button>(R.id.btnSaveScience).setOnClickListener {
            selectedScience.clear()
            boxes.forEach { if (it.isChecked) selectedScience.add(it.text.toString()) }
            popup.dismiss()
        }

        popup.setOnDismissListener { if (selectedScience.isEmpty()) mainCheckbox.isChecked = false }
        popup.showAtLocation(mainCheckbox, Gravity.CENTER, 0, 0)
    }

    private fun showtutor_date_popup(anchorView: android.view.View) {
        val view = LayoutInflater.from(this).inflate(R.layout.tutor_date_popup, null)
        val popup = PopupWindow(
            view,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        val boxes = listOf(
            view.findViewById<CheckBox>(R.id.cb_tuesday),
            view.findViewById<CheckBox>(R.id.cb_wednesday),
            view.findViewById<CheckBox>(R.id.cb_thursday)
        )

        boxes.forEach { it.isChecked = selectedDates.contains(it.text.toString()) }

        view.findViewById<Button>(R.id.btnSaveDate).setOnClickListener {
            selectedDates.clear()
            boxes.forEach { if (it.isChecked) selectedDates.add(it.text.toString()) }
            popup.dismiss()
        }

        popup.showAtLocation(anchorView, Gravity.CENTER, 0, 0)
    }

    private fun showtutor_recommendation_popup(anchorView: android.view.View) {
        val view = LayoutInflater.from(this).inflate(R.layout.tutor_recommendation_popup, null)
        val popup = PopupWindow(
            view,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        val etName = view.findViewById<EditText>(R.id.etTeacherName)
        val etEmail = view.findViewById<EditText>(R.id.etTeacherEmail)

        etName.setText(recommendedTeacherName)
        etEmail.setText(recommendedTeacherEmail)

        view.findViewById<Button>(R.id.btnSaveRecommendation).setOnClickListener {
            recommendedTeacherName = etName.text.toString()
            recommendedTeacherEmail = etEmail.text.toString()
            popup.dismiss()
        }

        popup.showAtLocation(anchorView, Gravity.CENTER, 0, 0)
    }

}
