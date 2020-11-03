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
    var location: String? = null

    var signedLocation: String? = null
    var feature: String? = null

    @SerializedName("date")
    var issueDate: String? = null

    @SerializedName("month")
    var issueMonth: String? = null;

    @SerializedName("year")
    var issueYear: String? = null;

    @SerializedName("identity_image")
    private val identityImage: String? = null

    @SerializedName("embeddings")

    private val embeddings: List<Int>? = null

    var issueLocation: String? = null

    @get:Exclude
    var storedFiles = ArrayList<File>(2)

    override fun toString(): String {
        return "IDCard {" +
                "\n ID= " + id +
                "\n Tên= '" + name + '\'' +
                "\n Ngày sinh= " + birthDay +
                "\n Nguyên quán= '" + location + '\'' +
                "\n Nơi ĐKHK thường trú= '" + location + '\'' +
                "\n Ngày đăng ký= '" + issueDate + "/" + issueMonth + "/" + issueYear + '\'' +
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
            location = idCard.location;
        } else {
            issueDate = idCard.issueDate;
            issueMonth = idCard.issueMonth;
            issueYear = idCard.issueYear;
        }
    }

    var signedDate = "$issueDate/$issueMonth/$issueYear";
}