package kr.ac.kpu.dailystone

import android.content.Context

class AppLock(context: Context) {

    private var sharedPref = context.getSharedPreferences("appLock",Context.MODE_PRIVATE)

    // 2차 비밀번호 설정
    fun setPassLock(password : String){
        sharedPref.edit().apply{
            putString("appLock", password)
            apply()
        }
    }

    // 2차 비밀번호 설정 제거
    fun removePassLock(){
        sharedPref.edit().apply{
            remove("appLock")
            apply()

        }    }

    //입력한 비밀번호 맞는지 체크
    fun checkPassLock(password: String) : Boolean{
        return sharedPref.getString("appLock","0") == password
    }

    fun isPassLockSet(): Boolean{
        if(sharedPref.contains("appLock")){
            return true
        }
        return false

    }}