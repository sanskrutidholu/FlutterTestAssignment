package com.example.fluttertestproject.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.fluttertestproject.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_login)

        binding.loginNextBtn.setOnClickListener {
            val phoneNumber = binding.ccp.selectedCountryCodeWithPlus + binding.loginPhoneEditText.text
            val country = binding.ccp.selectedCountryName

            if (phoneNumber!="") {
                val intent = Intent(this, VerifyCodeActivity::class.java)
                intent.putExtra("PHONE", phoneNumber)
                intent.putExtra("COUNTRY_NAME", country)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Empty Fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}