package kr.ac.kpu.dailystone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        auth = Firebase.auth
        btnConfirm.setOnClickListener {
            if(etIdInput.text.toString() == "" || etPassInput.text.toString() == "" ||
                    etPassReInput.text.toString() == ""){
                Toast.makeText(this, "아이디나 패스워드가 비어있습니다. 다시 입력해주세요", Toast.LENGTH_SHORT).show()
            }else if(etPassInput.text.toString() != etPassReInput.text.toString()){
                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            }else{
                createEmail(etIdInput, etPassInput)
            }
        }
        btnCancel.setOnClickListener {
            finish()
        }

    }

    private fun createEmail(etEmail : EditText, etPass : EditText){
        auth.createUserWithEmailAndPassword(etEmail.text.toString(), etPass.text.toString()).addOnCompleteListener {task ->
            if(task.isSuccessful){
                Toast.makeText(baseContext, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(baseContext, "회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

}