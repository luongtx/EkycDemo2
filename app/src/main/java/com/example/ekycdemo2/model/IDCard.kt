package com.example.ekycdemo2.model

class IDCard {
    var id: Int? = null
    var name: String? = null
    var birthDay: String? = null
    var location: String? = null
    var signedLocation: String? = null
    var feature: String? = null
    var issueDate: String? = null
    var issueLocation: String? = null
    var facing: Int? = 1;
    override fun toString(): String {
        return "IDCard {" +
                "\n id= " + id +
                "\n name= '" + name + '\'' +
                "\n birthDay= " + birthDay +
                "\n location= '" + location + '\'' +
                "\n signedLocation= '" + signedLocation + '\'' +
                "\n feature= '" + feature + '\'' +
                "\n issueDate= '" + issueDate + '\'' +
                "\n issueLocation= '" + issueLocation + '\'' +
                "\n}"
    }

    companion object {
        const val FRONT = 1;
        const val BACK = 2;
    }
}