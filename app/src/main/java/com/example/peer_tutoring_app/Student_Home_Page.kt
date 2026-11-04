package com.example.peer_tutoring_app


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.peer_tutoring_app.data.SessionAdapter
import com.example.peer_tutoring_app.model.TutorSession
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class Student_Home_Page : AppCompatActivity() {


    private lateinit var upcomingAdapter: SessionAdapter
    private val upcomingList = ArrayList<TutorSession>()
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_student_home_page)


        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        setupHorizontalRecyclerView()
        loadDummyData()
        fetchStudentName()
        setupLogout()

    }

    fun onclick_tutor_listing(view: View) {
        startActivity(Intent(this, Tutor_Listing::class.java))
    }


    private fun setupLogout() {
        val logoutBtn = findViewById<ImageView>(R.id.student_log_out_btn)
        logoutBtn.setOnClickListener {
            auth.signOut()
            val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            sharedPref.edit().remove("student_name").apply()

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }


    private fun fetchStudentName() {
        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        
        // 1. Try to get name from Intent (fastest after Sign Up)
        val intentName = intent.getStringExtra("USER_NAME")
        if (intentName != null) {
            tvWelcome.text = "Hello $intentName!"
            // Save to cache for next time
            sharedPref.edit().putString("student_name", intentName).apply()
            return
        }

        // 2. Try to get name from SharedPreferences (fastest for returning users)
        val cachedName = sharedPref.getString("student_name", null)
        if (cachedName != null) {
            tvWelcome.text = "Hello $cachedName!"
        }

        // 3. Always refresh/fetch from Firestore in background to ensure it's up to date
        val currentUser = auth.currentUser
        val email = currentUser?.email

        if (email != null) {
            db.collection("Students").document(email).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("Name")
                        if (name != null) {
                            tvWelcome.text = "Hello $name!"
                            sharedPref.edit().putString("student_name", name).apply()
                        }
                    }
                }
        }
    }


    private fun setupHorizontalRecyclerView() {
        val rvUpcoming = findViewById<RecyclerView>(R.id.rvUpcoming)
        // Set to Vertical (default) and disable nested scrolling so it works smoothly inside NestedScrollView
        rvUpcoming.layoutManager = LinearLayoutManager(this)
        rvUpcoming.isNestedScrollingEnabled = false
        upcomingAdapter = SessionAdapter(upcomingList, this)
        rvUpcoming.adapter = upcomingAdapter
    }


    private fun loadDummyData() {

        upcomingList.add(TutorSession("James Anderson", "Physics", "Jun 17, 2026"))
        upcomingList.add(TutorSession("Mary Parker", "English Literature", "Jun 16, 2026"))
        upcomingList.add(TutorSession("Billy Bob", "Calculus", "Jun 15, 2026"))
        upcomingList.add(TutorSession("Elizabeth Thompson", "Chemistry", "Jun 18, 2026"))
        upcomingList.add(TutorSession("Michael Richards", "World History", "Jun 19, 2026"))

        upcomingAdapter.notifyDataSetChanged()
    }
}
