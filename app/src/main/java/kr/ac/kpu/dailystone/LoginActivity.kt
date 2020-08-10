package kr.ac.kpu.dailystone

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var keyboardVisibilityUtils : KeyboardVisibilityUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        keyboardVisibilityUtils = KeyboardVisibilityUtils(window,
            onShowKeyboard = {keyboardHeight ->
            sv_root.run{
                smoothScrollTo(scrollX, scrollY + keyboardHeight)
            }
        })
        // Initialize Firebase Auth
        auth = Firebase.auth
        if (auth.currentUser != null) {
            // User is signed in (getCurrentUser() will be null if not signed in)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnLogin.setOnClickListener {
            if(etIdInput.text.toString() == "" || etPassInput.text.toString() == ""){
                Toast.makeText(this, "아이디와 비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show()
                hideKeyboard()
            } else{
                loginEmail(etIdInput, etPassInput)
                hideKeyboard()
            }
        }
        btnSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            hideKeyboard()
        }
        constraintLayout.setOnClickListener{
            hideKeyboard()
        }
    }


    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    override fun onDestroy() {
        keyboardVisibilityUtils.detachKeyboardListeners()
        super.onDestroy()
    }

    private fun loginEmail(etEmail : EditText, etPass : EditText){
        auth.signInWithEmailAndPassword(etEmail.text.toString(), etPass.text.toString()).addOnCompleteListener(this) { task ->
            if(task.isSuccessful){
                //다음 화면으로 넘어가는 코드
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
                Toast.makeText(this, "로그인이 완료되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                //로그인에 실패했을 때 넣는 코드
                Toast.makeText(this, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(user : FirebaseUser?){

    }
    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        if (currentFocus == null) View(this) else currentFocus?.let { hideKeyboard(it) }
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}