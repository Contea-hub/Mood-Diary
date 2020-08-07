package kr.ac.kpu.dailystone

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
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


        btnAuth.setOnClickListener {
            var existingEmail: String? = etEmailChange.text.toString()
            var existingPassword: String? = etPasswordChange.text.toString()

            if (!TextUtils.isEmpty(existingEmail) && !TextUtils.isEmpty(existingPassword)) {
                //if(originalEmail == existingEmail) {
                    loginEmail(etEmailChange,etPasswordChange)
                    /*val credential = EmailAuthProvider
                        .getCredential(existingEmail!!, existingPassword!!)
                    user?.reauthenticate(credential)
                        ?.addOnCompleteListener {
                            Toast.makeText(this, "인증이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                            tvNewEmail.visibility = View.VISIBLE
                            tvNewPassword.visibility = View.VISIBLE
                            etNewEmail.visibility = View.VISIBLE
                            etNewPassword.visibility = View.VISIBLE
                            btnChangeAccept.visibility = View.VISIBLE
                        }*/
                //} else{
                    //Toast.makeText(this, "이메일이 다릅니다.", Toast.LENGTH_SHORT).show()
                    
               // }
            }else{
                Toast.makeText(this, "이메일과 비밀번호 빈칸이 없어야 합니다.", Toast.LENGTH_SHORT).show()
            }
        }

        btnChangeAccept.setOnClickListener {
            val intent1 = Intent(this, UserInfoActivity::class.java)
            ChangeEmail = etNewEmail.text.toString()
            ChangePassword = etNewPassword.text.toString()
            emailUpdate(ChangeEmail)
            passwordUpdate(ChangePassword)
            intent1.putExtra("email", ChangeEmail)
            intent1.putExtra("password", ChangePassword)
            Toast.makeText(this, "이메일과 비밀번호 빈칸이 없어야 합니다.", Toast.LENGTH_SHORT).show()
            signOut()
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
                Toast.makeText(this, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show()
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