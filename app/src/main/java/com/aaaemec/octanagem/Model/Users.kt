package com.aaaemec.octanagem.Model

class Users {

    private var username: String? = null
    private var profileImageUrl: String? = null
    private var status: String? = null

    fun Users() {

    }

    fun Users(username: String?, profileImageUrl: String?, status: String?) {
        this.username = username
        this.profileImageUrl = profileImageUrl
        this.status = status
    }

    fun getusername(): String?{
        return username

    }
    fun getprofileImageUrl(): String?{
        return profileImageUrl

    }
    fun getstatus(): String?{
        return status

    }

    fun setusername() {
        username = username

    }
    fun setprofileImageUrl(){
        profileImageUrl = profileImageUrl

    }
    fun setstatus(){
        status = status

    }
}