package com.example.fluttertestproject.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.widget.Toast
import com.example.fluttertestproject.databinding.ActivityVerifyCodeBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class VerifyCodeActivity : AppCompatActivity() {

    private lateinit var binding : ActivityVerifyCodeBinding

    private lateinit var phoneNumber : String
    private lateinit var countryName : String

    lateinit var auth: FirebaseAuth

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setContentView(R.layout.activity_verify_code)

        phoneNumber = intent.getStringExtra("PHONE").toString()
        countryName = intent.getStringExtra("COUNTRY_NAME").toString()

        binding.verifyPhoneNumberTextView.text = "Code sent to: " +
                phoneNumber.substring(0,phoneNumber.length - 10) +
                "-" +
                phoneNumber.substring(phoneNumber.length-10,phoneNumber.length)+
                " | "

        sendCode(phoneNumber)

        binding.verifyOtpBtn.setOnClickListener {
            val otp = binding.verifyOtpEditText.text.toString()
            if (otp!="") {
                verifyVerificationCode(otp)
            }
        }

        binding.notYouBtn.setOnClickListener {
            finish()
        }

        binding.resendCodeTextView.setOnClickListener {
            resendVerificationCode(phoneNumber, resendToken!!)
        }

    }

    private fun sendCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

        // Start the countdown
        startCountdown()

    }

    var storedVerificationId: String? = null
    var resendToken: PhoneAuthProvider.ForceResendingToken? = null

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential : PhoneAuthCredential) {
            Log.d("VerifyCodeActivity", "onVerificationCompleted:$credential")
            Toast.makeText(this@VerifyCodeActivity, "Success", Toast.LENGTH_SHORT).show()
            signInWithPhoneAuthCredential(credential)

        }

        override fun onVerificationFailed(e : FirebaseException) {
            Log.w("VerifyCodeActivity", "onVerificationFailed", e)
            if (e is FirebaseAuthInvalidCredentialsException) {
                Toast.makeText(this@VerifyCodeActivity, e.message, Toast.LENGTH_LONG).show()
            } else if (e is FirebaseTooManyRequestsException) {
                Toast.makeText(this@VerifyCodeActivity, e.message, Toast.LENGTH_LONG).show()
            }
            Toast.makeText(this@VerifyCodeActivity, "Failed", Toast.LENGTH_SHORT).show()
        }

        override fun onCodeSent(verificationId : String, token: PhoneAuthProvider.ForceResendingToken) {
            Log.d("VerifyCodeActivity", "onCodeSent:$verificationId")
            storedVerificationId = verificationId
            resendToken = token
            Toast.makeText(this@VerifyCodeActivity, "Code Sent.", Toast.LENGTH_SHORT).show()
        }

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    Toast.makeText(this, "Success", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, UserNameSelectionActivity::class.java)
                    intent.putExtra("PHONE", phoneNumber)
                    intent.putExtra("COUNTRY_NAME", countryName)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)

                } else {

                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(this, "Incorrect OTP", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    private fun startCountdown(){

        binding.resendCodeTextView.isClickable = false
        val timer = object: CountDownTimer(60000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                binding.resendCodeTextView.text = "Wait until: ${(millisUntilFinished/1000)} sec"
            }

            override fun onFinish() {
                val content = SpannableString("Resend")
                content.setSpan(UnderlineSpan(), 0, content.length, 0)
                binding.resendCodeTextView.text = content
                binding.resendCodeTextView.isClickable = true
            }
        }
        timer.start()

    }

    private fun verifyVerificationCode(otp: String) {
        val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, otp)
        signInWithPhoneAuthCredential(credential)

    }

    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken
    ) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,  // Phone number to verify
            60,  // Timeout duration
            TimeUnit.SECONDS,  // Unit of timeout
            this,  // Activity (for callback binding)
            callbacks,  // OnVerificationStateChangedCallbacks
            token
        ) // ForceResendingToken from callbacks

        Toast.makeText(this, "Sending New Code...", Toast.LENGTH_SHORT).show()
        startCountdown()
    }


}