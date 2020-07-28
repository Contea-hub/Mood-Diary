package kr.ac.kpu.dailystone

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.dialog_happy.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class DialogAddFragment(context: Context) : Dialog(context) {
    private var mAuth: FirebaseAuth? = null
    val database : FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var db: DatabaseReference
    private lateinit var level : Any
    private lateinit var diary : Any

    private val current: LocalDate = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    val formatted = current.format(formatter)
    var user: FirebaseUser?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_happy)

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().currentUser

        db=Firebase.database.reference
        var uid = user?.uid.toString()



        dhBtnH1.setOnClickListener{
            dhEdHl.setText("5")
            level = dhEdHl.text.toString()
        }

        dhBtnH2.setOnClickListener{
            dhEdHl.setText("10")
            level = dhEdHl.text.toString()
        }

        dhBtnH3.setOnClickListener{
            dhEdHl.setText("15")
            level = dhEdHl.text.toString()
        }

        dhBtnYes.setOnClickListener {
            // 데이터베이스에 저장
            onWriteDBPost()
            Toast.makeText(context,"저장 완료",Toast.LENGTH_SHORT)
            dismiss()
        }
        dhBtnNo.setOnClickListener {
            Toast.makeText(context,"취소",Toast.LENGTH_SHORT)
            dismiss();
        }
    }

    private fun onWriteDBPost() {
        db = Firebase.database.reference
        var user = FirebaseAuth.getInstance().currentUser
        level = dhEdHl.text.toString()
        diary = dhEdDiary.text.toString()

        val myRef = database.getReference(user?.uid.toString())
        val key = myRef.push().key

        val postValues: HashMap<String, Any> = HashMap()
        postValues["level"] = level
        postValues["diary"] = diary

        myRef.child(formatted).child(key!!).setValue(postValues)
    }

}