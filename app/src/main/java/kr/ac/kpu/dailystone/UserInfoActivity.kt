package kr.ac.kpu.dailystone

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_change.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_user_info.*
import kotlinx.android.synthetic.main.fragment_userinfo.*
import kr.ac.kpu.dailystone.AppLockConst.CHANGE_PASSWORD
import kr.ac.kpu.dailystone.AppLockConst.DISABLE_PASSLOCK
import kr.ac.kpu.dailystone.AppLockConst.ENABLE_PASSLOCK

class UserInfoActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContentView(R.layout.activity_user_info)
        tvChEmail.text = auth?.currentUser?.email
        tvChPassword.transformationMethod = PasswordTransformationMethod.getInstance()
        btnBack.setOnClickListener {
            finish()
        }

        btnChangeProfile.setOnClickListener {
            Log.d("Min: plz", "jaebal: plz")
            val intent = Intent(this, ChangeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun getExtra() {
        if (intent.hasExtra("email") && intent.hasExtra("password")) {
            tvChEmail.text = intent.getStringExtra("email")
            tvChPassword.text = intent.getStringExtra("password")
        } else {

        }
    }

    override fun onResume() {
        super.onResume()
        getExtra()
    }

}