package com.example.ekycdemo2.model

import com.google.firebase.database.Exclude
import com.google.gson.annotations.SerializedName
import java.io.File
import java.util.*


class IDCard {

    var id: String? = null

    @SerializedName("fullname")
    var name: String? = null

    @SerializedName("dob")
    var birthDay: String? = null

    @SerializedName("add")
    var address: String? = null

    var signedAdd: String? = null
    var feature: String? = null

    @SerializedName("date")
    var issuedDay: String? = null

    @SerializedName("month")
    var issueMonth: String? = null;

    @SerializedName("year")
    var issueYear: String? = null;

    @SerializedName("identity_image")
    private val identityImage: String? = null

    @SerializedName("embeddings")

    private val embeddings: List<Int>? = null

    var issuedAdd: String? = null

    @get:Exclude
    var storedFiles = ArrayList<File>(2)

    override fun toString(): String {
        return "IDCard {" +
                "\n ID= " + id +
                "\n Tên= '" + name + '\'' +
                "\n Ngày sinh= " + birthDay +
                "\n Nguyên quán= '" + address + '\'' +
                "\n Nơi ĐKHK thường trú= '" + address + '\'' +
                "\n Ngày đăng ký= '" + issuedDay + "/" + issueMonth + "/" + issueYear + '\'' +
                "\n}"
    }

    companion object {
        const val FRONT = 0;
        const val BACK = 1;
    }

    fun extract(idCard: IDCard) {
        if (!idCard.id.isNullOrEmpty()) {
            id = idCard.id;
            name = idCard.name;
            birthDay = idCard.birthDay;
            address = idCard.address;
        } else {
            issuedDay = idCard.issuedDay;
            issueMonth = idCard.issueMonth;
            issueYear = idCard.issueYear;
        }
    }

    var issuedDate = "$issuedDay/$issueMonth/$issueYear";
}