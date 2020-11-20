package com.example.ekycdemo2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.ekycdemo2.utils.Constants
import com.example.ekycdemo2.utils.Constants.Companion.userPN
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_main.btn_confirm
import kotlinx.android.synthetic.main.activity_otp_authentication.*
import java.util.concurrent.TimeUnit

class OTPAuthentication : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_authentication)
        sendOTP(userPN)
        btn_confirm.setOnClickListener {
            val userCode = etOTP.text.toString();
            if (userCode.isNotEmpty()) {
                verifyUserCode(userCode);
            }
        }
    }


    private lateinit var verifyCodeBySystem: String

    private fun verifyUserCode(userCode: String) {
        val phoneAuthCredential = PhoneAuthProvider.getCredential(verifyCodeBySystem, userCode);
        signInWithPhoneAuthCredential(phoneAuthCredential);
    }

    private fun sendOTP(phoneNumber: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "+84$phoneNumber", // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this, // Activity (for callback binding)
            callbacks
        ) // OnVerificationStateChangedCallbacks
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d(Constants.TAG, "onVerificationCompleted:$credential")

//            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(Constants.TAG, "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                // ...
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                // ...
            }

            // Show a message and update the UI
            // ...
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(Constants.TAG, "onCodeSent:$verificationId")

            // Save verification ID and resending token so we can use them later
            verifyCodeBySystem = verificationId

            // ...
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        val firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.setLanguageCode("vi");
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                startActivity(Intent(this, ActivityDocumentOption::class.java));
            } else {
                Toast.makeText(this@OTPAuthentication, it.exception?.message, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
}