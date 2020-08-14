package kr.ac.kpu.dailystone

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.dialog_happy.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class DialogSadAddFragment(context: Context) : Dialog(context) {
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
        setContentView(R.layout.dialog_sad)

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().currentUser

        db=Firebase.database.reference
        var uid = user?.uid.toString()
        readID(uid)


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

    fun onWriteDBPost() {
        db = Firebase.database.reference
        var user = FirebaseAuth.getInstance().currentUser
        level = dhEdHl.text.toString()
        diary = dhEdDiary.text.toString()
        //val myRef = database.getReference("posts")
        val myRef = database.getReference(user?.uid.toString())

        val postValues: HashMap<String, Any> = HashMap()
        postValues["date"] = formatted
        postValues["level"] = level
        postValues["diary"] = diary
        /* id = readID()
         postValues["id"] = ++id*/
        myRef.setValue(postValues)
    }

    fun readID(uid:String): String {
        db = Firebase.database.reference
        var Did: String="1"
        db.child(uid).child("20200727").runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                if((mutableData.child("Did").value as? Long)!=null){
                    Did= mutableData.child("Did").value as String
                }else{
                    Did="1"
                }

                return Transaction.success(mutableData);
            }

            override fun onComplete(
                databaseError: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                //Toast.makeText(applicationContext,"Transaction:onComplete:$databaseError",Toast.LENGTH_LONG).show()
            }
        })
        return Did
    }
}