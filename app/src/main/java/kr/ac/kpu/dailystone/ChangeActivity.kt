package kr.ac.kpu.dailystone

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_change.*

class ChangeActivity : AppCompatActivity() {

    private val user : FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change)

        var ChangeEmail: String
        var ChangePassword: String

        etPasswordChange.transformationMethod = PasswordTransformationMethod.getInstance()
        etNewPassword.transformationMethod = PasswordTransformationMethod.getInstance()


        btnAuth.setOnClickListener {
            var existingEmail: String? = etEmailChange.text.toString()
            var existingPassword: String? = etPasswordChange.text.toString()
            if (!TextUtils.isEmpty(existingEmail) && !TextUtils.isEmpty(existingPassword)) {

                loginEmail(etEmailChange,etPasswordChange)

            }else{
                Toast.makeText(this, "이메일과 비밀번호 빈칸이 없어야 합니다.", Toast.LENGTH_SHORT).show()
            }
        }

        btnChangeAccept.setOnClickListener {
            val intent1 = Intent(this, UserInfoActivity::class.java)
            val intent2 = Intent(this, LoginActivity::class.java)
            ChangeEmail = etNewEmail.text.toString()
            ChangePassword = etNewPassword.text.toString()
            if(etEmailChange.text.toString() == ChangeEmail &&  etPasswordChange.text.toString() == ChangePassword ){
                Toast.makeText(this, "새로운 이메일과 비밀번호가 기존과 같습니다.", Toast.LENGTH_SHORT).show()
            }
            else {
                emailUpdate(ChangeEmail)
                passwordUpdate(ChangePassword)
                intent1.putExtra("email", ChangeEmail)
                intent1.putExtra("password", ChangePassword)
                signOut()
                startActivity(intent2)


            }
        }

        btnChangeCancel.setOnClickListener {
            finish()
            Toast.makeText(this, "이메일 비밀번호 변경 취소", Toast.LENGTH_SHORT).show()
        }


    }

    fun emailUpdate(newEmail: String) {
        user?.updateEmail(newEmail)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "이메일 비밀번호 변경 완료", Toast.LENGTH_SHORT).show()
                    Log.d("Min", "User email address updated.")

                }
            }
    }

    fun passwordUpdate(newPassword: String) {
        user?.updatePassword(newPassword)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Min", "User password updated.")
                }
            }
    }
    private fun loginEmail(etEmailChange : EditText, etPasswordChange : EditText){
        auth = Firebase.auth
        auth.signInWithEmailAndPassword(etEmailChange.text.toString(), etPasswordChange.text.toString()).addOnCompleteListener(this) { task ->
            if(task.isSuccessful){
                tvNewEmail.visibility = View.VISIBLE
                tvNewPassword.visibility = View.VISIBLE
                etNewEmail.visibility = View.VISIBLE
                etNewPassword.visibility = View.VISIBLE
                btnChangeAccept.visibility = View.VISIBLE
                Toast.makeText(this, "인증이 완료되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                //로그인에 실패했을 때 넣는 코드
                Toast.makeText(this, "인증에 실패하였습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signOut(){
        val intent = Intent(this, LoginActivity::class.java)
        FirebaseAuth.getInstance().signOut()
        startActivity(intent)
        this?.finish()
    }
}