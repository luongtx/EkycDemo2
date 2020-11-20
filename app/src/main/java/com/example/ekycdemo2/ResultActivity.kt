package com.example.ekycdemo2

import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.ekycdemo2.IDCardScannerActivity.Companion.idCard
import com.example.ekycdemo2.repos.impl.IDCardRepositoryImpl
import com.example.ekycdemo2.utils.Constants
import kotlinx.android.synthetic.main.activity_result.*
import java.io.File


class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        try {
            img_card_front.setImageURI(Uri.fromFile(idCard.storedFiles[0]))
            img_card_back.setImageURI(Uri.fromFile(idCard.storedFiles[1]))
        } catch (e: Exception) {
            Log.d(Constants.TAG, e.message.toString());
        }
        val sharedReferenced = getSharedPreferences("prefs", MODE_PRIVATE)
        val pathFace = sharedReferenced.getString("img_face", "")
        img_face.setImageURI(Uri.fromFile(File(pathFace!!)))
        displayIDCardInfo();

    }

    private fun displayIDCardInfo() {
        tvID.setText(idCard.id);
        tvName.setText(idCard.name);
        tvDOB.setText(idCard.dob);
        tvAdd.setText(idCard.address);
        tvIssuedDate.setText(idCard.issuedDate);
        tvIssuedAdd.setText(idCard.issuedAddress);

        editId.setOnClickListener { enableEditText(tvID) }
        editName.setOnClickListener { enableEditText(tvName) }
        editDob.setOnClickListener { enableEditText(tvDOB) }
        editAdd.setOnClickListener { enableEditText(tvAdd) }
        editIssuedDate.setOnClickListener { enableEditText(tvIssuedDate) }
        editIssuedAdd.setOnClickListener { enableEditText(tvIssuedAdd) }

        btnSave.setOnClickListener { saveIdCard() }

    }

    private fun saveIdCard() {
        idCard.id = tvID.text.toString();
        idCard.name = tvName.text.toString();
        idCard.dob = tvDOB.text.toString();
        idCard.address = tvAdd.text.toString();
        idCard.issuedDate = tvIssuedDate.text.toString();
        idCard.issuedAddress = tvIssuedAdd.text.toString();
        disableAll();
        val idCardRepository = IDCardRepositoryImpl();
        idCardRepository.saveIDCard(idCard);
        SweetAlertDialog(
            this, SweetAlertDialog.SUCCESS_TYPE
        )
            .setTitleText("Success!")
            .setContentText("Giao dịch thành công!")
            .show()
    }

    private fun disableAll() {
        disableEditText(tvID);
        disableEditText(tvName);
        disableEditText(tvDOB);
        disableEditText(tvAdd);
        disableEditText(tvIssuedDate);
        disableEditText(tvIssuedAdd);
    }

    private fun enableEditText(editText: EditText) {
        editText.isEnabled = true;
        editText.inputType = InputType.TYPE_CLASS_TEXT;
        editText.isFocusable = true;
    }

    private fun disableEditText(editText: EditText) {
        editText.isEnabled = false;
        editText.isFocusable = false;
    }
}