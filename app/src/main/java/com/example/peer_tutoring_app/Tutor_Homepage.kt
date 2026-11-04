package com.example.peer_tutoring_app


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.peer_tutoring_app.data.StudentSessionAdapter
import com.example.peer_tutoring_app.model.StudentSession
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class Tutor_Homepage : AppCompatActivity() {


    private lateinit var sessionAdapter: StudentSessionAdapter
    private val sessionList = ArrayList<StudentSession>()
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tutor_homepage)


        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        setupData()
        setupRecyclerView()
        fetchTutorName()
        setupLogout()
    }


    private fun setupLogout() {
        val logoutBtn = findViewById<ImageView>(R.id.tutor_log_out_btn)
        logoutBtn.setOnClickListener {
            auth.signOut()

            // Clear cache
            val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            sharedPref.edit().remove("tutor_name").apply()

            // Navigate back to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }


    private fun fetchTutorName() {
        val tvHello = findViewById<TextView>(R.id.tvHello)
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        // 1. Try to get name from Intent
        val intentName = intent.getStringExtra("USER_NAME")
        if (intentName != null) {
            tvHello.text = "Hello $intentName!"
            sharedPref.edit().putString("tutor_name", intentName).apply()
            return
        }

        // 2. Try to get name from Cache
        val cachedName = sharedPref.getString("tutor_name", null)
        if (cachedName != null) {
            tvHello.text = "Hello $cachedName!"
        }

        // 3. Fetch from Firestore
        val currentUser = auth.currentUser
        val email = currentUser?.email

        if (email != null) {
            db.collection("Tutors").document(email).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("Name")
                        if (name != null) {
                            tvHello.text = "Hello $name!"
                            sharedPref.edit().putString("tutor_name", name).apply()
                        }
                    }
                }
        }
    }


    private fun setupData() {
        val names = arrayOf("William Thompson", "Sarah Parker", "Kevin Anderson", "James Miller", "Emily Davis")
        val courses = arrayOf("Math", "Math", "Web Design", "English", "Science")
        val times = arrayOf(
            "4:00 p.m-5:00 p.m",
            "6:00 p.m - 7:00 p.m",
            "9:00 a.m - 10:00 a.m",
            "1:00 p.m - 2:00 p.m",
            "3:00 p.m - 4:00 p.m"
        )


        for (i in names.indices) {
            val session = StudentSession(names[i], courses[i], times[i])
            sessionList.add(session)
        }
    }


    private fun setupRecyclerView() {
        val rvUpcomingSessions = findViewById<RecyclerView>(R.id.rvUpcomingSessions)
        rvUpcomingSessions.layoutManager = LinearLayoutManager(this)
        sessionAdapter = StudentSessionAdapter(sessionList)
        rvUpcomingSessions.adapter = sessionAdapter
    }
}



