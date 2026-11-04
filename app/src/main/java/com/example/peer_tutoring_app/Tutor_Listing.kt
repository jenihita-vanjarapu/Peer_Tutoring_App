package com.example.peer_tutoring_app


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.peer_tutoring_app.data.TutorAdapter
import com.example.peer_tutoring_app.model.Tutor
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore


class Tutor_Listing : AppCompatActivity() {


    private lateinit var tutorAdapter: TutorAdapter
    private val allTutors = ArrayList<Tutor>()
    private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tutor_listing)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        setupRecyclerView()
        setupTabLayout()
        setupData()
    }
    fun back_to_main(view: View){
        startActivity(Intent(this, Student_Home_Page::class.java))

    }


    private fun setupData() {
        db.collection("Tutors")
            .get()
            .addOnSuccessListener { result ->
                allTutors.clear()
                for (document in result) {
                    val name = document.getString("Name") ?: ""
                    val email = document.getString("Email") ?: ""
                    val ela = document.get("ELA") as? List<String> ?: emptyList()
                    val math = document.get("Math") as? List<String> ?: emptyList()
                    val socialStudies = document.get("Social Studies") as? List<String> ?: emptyList()
                    val science = document.get("Science") as? List<String> ?: emptyList()
                    val availableDates = document.get("Available Dates") as? List<String> ?: emptyList()
                    val recommendedTeacherName = document.getString("Recommended Teacher Name") ?: ""
                    val recommendedTeacherEmail = document.getString("Recommended Teacher Email") ?: ""
                    
                    val subjects = ArrayList<String>()
                    if (ela.isNotEmpty()) {
                        subjects.add("English")
                        subjects.addAll(ela)
                    }
                    if (math.isNotEmpty()) {
                        subjects.add("Math")
                        subjects.addAll(math)
                    }
                    if (socialStudies.isNotEmpty()) {
                        subjects.add("Social Studies")
                        subjects.addAll(socialStudies)
                    }
                    if (science.isNotEmpty()) {
                        subjects.add("Science")
                        subjects.addAll(science)
                    }

                    val tutor = Tutor(
                        name = name,
                        email = email,
                        subjects = subjects,
                        availableDates = availableDates,
                        recommendedTeacherName = recommendedTeacherName,
                        recommendedTeacherEmail = recommendedTeacherEmail
                    )
                    allTutors.add(tutor)
                }
                tutorAdapter.updateList(allTutors)
            }
            .addOnFailureListener { exception ->
                Log.w("Tutor_Listing", "Error getting documents.", exception)
                Toast.makeText(this, "Failed to load tutors", Toast.LENGTH_SHORT).show()
            }
    }


    private fun setupRecyclerView() {
        val rvTutors = findViewById<RecyclerView>(R.id.rvTutors)
        rvTutors.layoutManager = LinearLayoutManager(this)
        tutorAdapter = TutorAdapter(allTutors)
        rvTutors.adapter = tutorAdapter
    }


    private fun setupTabLayout() {
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val category = tab?.text.toString()
                filterTutors(category)
            }


            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }


    private fun filterTutors(category: String) {
        if (category == "All") {
            tutorAdapter.updateList(allTutors)
        } else {
            val filteredList = allTutors.filter { it.subject.contains(category, ignoreCase = true) }
            tutorAdapter.updateList(filteredList)
        }
    }
}







