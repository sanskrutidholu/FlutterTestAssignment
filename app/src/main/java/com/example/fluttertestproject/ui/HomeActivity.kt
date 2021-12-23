package com.example.fluttertestproject.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.fluttertestproject.R
import com.example.fluttertestproject.fragments.AddVideoFragment
import com.example.fluttertestproject.fragments.ExploreFragment
import com.example.fluttertestproject.fragments.LibraryFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeActivity : AppCompatActivity() {

    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var auth: FirebaseAuth

    lateinit var userName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        userName = intent.getStringExtra("UserName").toString()

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        auth = FirebaseAuth.getInstance()

        FirebaseApp.initializeApp(this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            SafetyNetAppCheckProviderFactory.getInstance()
        )

        setUpBottomNavigation()

        // checking user logged in or not
        if (auth.uid == null) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        } else {
            checkUserExists()
        }
    }

    private fun checkUserExists() {
        val userRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(auth.uid!!)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value == null) {
                    val intent = Intent(applicationContext, LoginActivity::class.java)
                    startActivity(intent)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun setUpBottomNavigation() {

        val explore = ExploreFragment()
        val video = AddVideoFragment()
        val library = LibraryFragment()

        setCurrentFragment(explore)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.explore -> setCurrentFragment(explore)
                R.id.addVideo -> getExploreFragment(video)
                R.id.library -> setCurrentFragment(library)
            }
            true
        }

//        // for setting number of notification or messages in tab
//        bottomNavigationView.getOrCreateBadge(R.id.explore).apply {
//            number = 20
//            isVisible = true
//        }

    }

    private fun setCurrentFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            commit()
        }
    }

    private fun getExploreFragment(fragment: AddVideoFragment) {
        val mBundle = Bundle()
        mBundle.putString("UserName",userName)
        fragment.arguments = mBundle
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            commit()
        }
    }



}