package com.example.album.utils

import android.content.Context
import android.content.SharedPreferences
/*
File : 
Description :
*/
class PrefManager(context: Context) {

    private val preferences: SharedPreferences = context.getSharedPreferences("TDM", Context.MODE_PRIVATE)
    private val editor = preferences.edit()

    fun setQuery(q: String){
        editor.putString("query", q)
        editor.apply()
    }

    fun getQuery(): String{
        return preferences.getString("query", "india")!!
    }

//    fun setCurrentProfileDetails(profile: Profile) {
//
//        licenseEditor.putString("profile_email", profile.email)
//        licenseEditor.putString("profile_number", profile.mobNo)
//        licenseEditor.putString("profile_Address", profile.address)
//        licenseEditor.putString("profile_city", profile.city)
//        licenseEditor.putString("profile_pinCode", profile.pincode)
//        licenseEditor.apply()
//    }
//
//    fun getCurrentProfileDetails(): Profile {
//
//        val email = preferences.getString("profile_email", "abcdef123@gmail.com")
//        val number = preferences.getString("profile_number", "1234567890")
//        val address = preferences.getString("profile_Address", "sector 33, block d, Noida")
//        val city = preferences.getString("profile_city", "Noida")
//        val pinCode = preferences.getString("profile_pinCode", "123456")
//
//        return Profile(
//
//            email!!,
//            number!!,
//            address!!,
//            city!!,
//            pinCode!!
//        )
//    }
}