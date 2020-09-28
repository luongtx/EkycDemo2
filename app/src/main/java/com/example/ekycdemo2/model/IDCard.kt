package com.example.ekycdemo2.model

import android.net.Uri
import java.util.*

class IDCard {
    var id: Int? = null
    var name: String? = null
    var birthDay: String? = null
    var location: String? = null
    var signedLocation: String? = null
    var feature: String? = null
    var issueDate: String? = null
    var issueLocation: String? = null
    var storePaths = ArrayList<String>(2)
    override fun toString(): String {
        return "IDCard {" +
                "\n ID= " + id +
                "\n Tên= '" + name + '\'' +
                "\n Ngày sinh= " + birthDay +
                "\n Nguyên quán= '" + location + '\'' +
                "\n Nơi ĐKHK thường trú= '" + signedLocation + '\'' +
                "\n Đặc điểm nhận dạng= '" + feature + '\'' +
                "\n Ngày đăng ký= '" + issueDate + '\'' +
                "\n Nơi đăng ký= '" + issueLocation + '\'' +
                "\n}"
    }

    companion object {
        const val FRONT = 0;
        const val BACK = 1;
    }
}