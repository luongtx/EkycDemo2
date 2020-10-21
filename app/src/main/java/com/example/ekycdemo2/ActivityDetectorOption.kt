package com.example.ekycdemo2

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detector_option.*

class ActivityDetectorOption : AppCompatActivity() {
    enum class DocumentType {
        IDCard, DriverLicense, Passport
    }

    var documentType: DocumentType? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detector_option)
        btnCont.setOnClickListener { onBtnClick() };
        layout1.setOnClickListener {
            layout1.isActivated = true;
            layout2.isActivated = false;
            layout3.isActivated = false;
            documentType = DocumentType.IDCard;
        };
        layout2.setOnClickListener {
            layout1.isActivated = false;
            layout2.isActivated = true;
            layout3.isActivated = false;
            documentType = DocumentType.DriverLicense;
        };
        layout3.setOnClickListener {
            layout1.isActivated = false;
            layout2.isActivated = false;
            layout3.isActivated = true;
            documentType = DocumentType.Passport;
        };
    }

    private fun onBtnClick() {
        when {
            etName.text.isEmpty() -> {
                Toast.makeText(this, "Vui lòng nhập họ tên", Toast.LENGTH_LONG).show();
            }
            documentType == null -> {
                Toast.makeText(this, "Vui lòng chọn loại hồ sơ", Toast.LENGTH_LONG).show();
            }
            else -> {
                val intent: Intent?;
                when (documentType) {
                    DocumentType.IDCard -> {
                        intent = Intent(this, IDCardScannerActivity::class.java)
                        startActivity(intent);
                        finish()
                    };
                    DocumentType.DriverLicense, DocumentType.Passport -> Toast.makeText(
                        this,
                        "Coming soon...",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}