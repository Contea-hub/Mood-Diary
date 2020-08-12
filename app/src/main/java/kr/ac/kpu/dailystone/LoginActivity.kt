package kr.ac.kpu.dailystone

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.dialog_progress.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
class LoginActivity : AppCompatActivity() {
    companion object{
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
    private lateinit var auth : FirebaseAuth
    private lateinit var keyboardVisibilityUtils : KeyboardVisibilityUtils
    lateinit var database : DatabaseReference
    private val current: LocalDate = LocalDate.now()
    private var formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    private var formatted: String = current.format(formatter)
    var year = formatted.substring(2,4)
    var month = formatted.substring(4,6)
    val dlgView = layoutInflater.inflate(R.layout.dialog_progress , null)
    val progressDialog = AlertDialog.Builder(this)

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
            val uid = auth.uid
            database = Firebase.database.reference

            val postListener = object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.hasChildren()){

                    }else{
                        database.child(uid!!).child("goal").child(year).child(month).child("goal").setValue("15")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            }
            database.child(uid!!).child("goal").child(year).child(month).addListenerForSingleValueEvent(postListener)


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
                val uid = auth.uid
                database = Firebase.database.reference

                val postListener = object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.hasChildren()){

                        }else{
                            database.child(uid!!).child("goal").child(year).child(month).child("goal").setValue("15")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                }
                database.child(uid!!).child("goal").child(year).child(month).addListenerForSingleValueEvent(postListener)
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

}