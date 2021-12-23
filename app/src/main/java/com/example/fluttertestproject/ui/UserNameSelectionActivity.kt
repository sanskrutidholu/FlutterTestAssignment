package com.example.fluttertestproject.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.fluttertestproject.databinding.ActivityUserNameSelectionBinding
import com.example.fluttertestproject.firebaseClasses.FirebaseOperations
import com.google.firebase.auth.FirebaseAuth

class UserNameSelectionActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityUserNameSelectionBinding

    private lateinit var phoneNumber : String
    private lateinit var countryName : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserNameSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        phoneNumber = intent.getStringExtra("PHONE").toString()
        countryName = intent.getStringExtra("COUNTRY_NAME").toString()


        binding.nextBtn.setOnClickListener {
            val userName = binding.userName.text.toString()
            FirebaseOperations().registerUser(auth.currentUser!!.uid,userName,phoneNumber,countryName)
            Toast.makeText(this,"User Registered Successfully..",Toast.LENGTH_SHORT).show()

            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("UserName",userName)
            startActivity(intent)
            finish()
        }

//        val mFragmentManager = supportFragmentManager
//        val mFragmentTransaction = mFragmentManager.beginTransaction()
//        val mFragment = ExploreFragment()
//
//        binding.nextBtn.setOnClickListener {
//            val mBundle = Bundle()
//            mBundle.putString("UserName",binding.userName.text.toString())
//            mFragment.arguments = mBundle
//            mFragmentTransaction.add(R.id.frameLayout, mFragment).commit()
//        }

    }
}