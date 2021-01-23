package com.example.ekycdemo3

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ekycdemo3.utils.Constants
import com.example.ekycdemo3.utils.Constants.Companion.userPN
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_confirm.setOnClickListener {
            confirm()
        }

    }

    private fun allPermissionsGranted() = Constants.REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (requestCode == Constants.REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startActivity(Intent(this, OTPAuthentication::class.java));
            } else {
                Toast.makeText(
                    this,
                    "Vui lòng cấp phép các quyền cho ứng dụng.",
                    Toast.LENGTH_SHORT
                ).show();
                ActivityCompat.requestPermissions(
                    this, Constants.REQUIRED_PERMISSIONS, Constants.REQUEST_CODE_PERMISSIONS
                )
            }
        }
    }

    private fun confirm() {
        if (et_phone.text.isEmpty() || et_email.text.isEmpty()) {
            Toast.makeText(this, getString(R.string.enter_phone_email), Toast.LENGTH_SHORT).show()
            return
        }
        saveData();
        //request permission
        if (allPermissionsGranted()) {
            startActivity(Intent(this, OTPAuthentication::class.java));
        } else {
            ActivityCompat.requestPermissions(
                this, Constants.REQUIRED_PERMISSIONS, Constants.REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun saveData() {
        userPN = et_phone.text.toString();
//        val mFirebaseReference = Firebase.database.reference
//        mFirebaseReference.child(ROOT_NODE).child(userPN).push()
    }
}