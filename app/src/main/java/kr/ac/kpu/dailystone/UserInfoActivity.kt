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
        var editFlagEmail: Boolean = false      // 에디트 버튼을 누를 때 확인 플래그
        var editFlagPassword: Boolean = false
        auth = Firebase.auth
        setContentView(R.layout.activity_user_info)
        //init()
        tvChEmail.text = auth?.currentUser?.email
        //  tvChPassword.text = LoginActivity().etPassInput.text.toString()

        val user = FirebaseAuth.getInstance().currentUser

            btnBack.setOnClickListener {
                finish()
            }

            /*btnEditEmail.setOnClickListener {
                if (editFlagEmail == false) {
                    editFlagEmail = true
                    tvChEmail.visibility = View.INVISIBLE
                    etChEmail.visibility = View.VISIBLE


                } else {
                    editFlagEmail = false
                    //tvChEmail.text = etChEmail.text
                    tvChEmail.visibility = View.VISIBLE
                    etChEmail.visibility = View.INVISIBLE
                }
            }*/

            btnEditPassword.setOnClickListener {
                if (editFlagPassword == false) {
                    editFlagPassword = true
                    tvChPassword.visibility = View.INVISIBLE
                    etChPassword.visibility = View.VISIBLE

                } else {
                    editFlagPassword = false
                    //tvChPassword.text = etChPassword.text
                    tvChPassword.visibility = View.VISIBLE
                    etChPassword.visibility = View.INVISIBLE
                }
            }

            btnEditEmail.setOnClickListener {
                Log.d("Min: plz", "jaebal: plz")
                val intent = Intent(this,ChangeActivity::class.java)
                startActivity(intent)
                finish()
            }

            /*btnSavePw.setOnClickListener {
                /*etChPassword.clearFocus()
                updatePassword(etChPassword.text.toString())
                tvChPassword.text = etChPassword.text*/
                var editTextNewPassword = EditText(this)
                editTextNewPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                var alertDialog = AlertDialog.Builder(this)
                alertDialog.setTitle("패스워드 변경")
                alertDialog.setMessage("변경하고 싶은 패스워드를 입력하세요")
                alertDialog.setView(editTextNewPassword)
                alertDialog.setPositiveButton("변경") { _, _ -> updatePassword(editTextNewPassword.text.toString()) }
                alertDialog.setNegativeButton("취소") { dialogInterface, _ -> dialogInterface.dismiss() }
                alertDialog.show()
        }*/

        }
    fun getExtra() {
        //val intent = Intent(this,ChangeActivity::class.java)

        if (intent.hasExtra("email") && intent.hasExtra("password")) {
            tvChEmail.text = intent.getStringExtra("email")
            tvChPassword.text = intent.getStringExtra("password")
        } else {
            Toast.makeText(this, "Error!!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        getExtra()
    }

}