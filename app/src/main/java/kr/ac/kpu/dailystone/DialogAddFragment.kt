package kr.ac.kpu.dailystone

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import java.util.*
import kotlin.collections.HashMap

@RequiresApi(Build.VERSION_CODES.O)
class DialogAddFragment(context: Context) : Dialog(context) {
    private var mAuth: FirebaseAuth? = null
    val database : FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var db: DatabaseReference
    private lateinit var level : Any
    private lateinit var diary : Any

    private val current: LocalDate = LocalDate.now()
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    private val formatted: String = current.format(formatter)
    var date = formatted.substring(2,8)
    private val formatterYear: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy")
    private val yearformatted: String = current.format(formatterYear)
    var year = yearformatted.substring(2,4)
    private val formatterMonth : DateTimeFormatter = DateTimeFormatter.ofPattern("MM")
    private val monthformatted: String = current.format(formatterMonth)
    private val formatterDay : DateTimeFormatter = DateTimeFormatter.ofPattern("dd")
    private val dayformatted: String = current.format(formatterDay)
    var user: FirebaseUser?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_happy)

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().currentUser

        db=Firebase.database.reference
        var uid = user?.uid.toString()
        //readID(uid)


        dhBtnH1.setOnClickListener{
            dhEdHl.setText("30")
            level = dhEdHl.text.toString()
        }

        dhBtnH2.setOnClickListener{
            dhEdHl.setText("60")
            level = dhEdHl.text.toString()
        }

        dhBtnH3.setOnClickListener{
            dhEdHl.setText("100")
            level = dhEdHl.text.toString()
        }

        dhBtnDice.setOnClickListener {
            var rnd = Random()
            var num = rnd.nextInt(15)
            dhEdHl.setText(num.toString())
            level = dhEdHl.text.toString()

        }

        dhBtnYes.setOnClickListener {
            // 데이터베이스에 저장
            onWriteDBPost()
            dismiss()
        }
        dhBtnNo.setOnClickListener {
            dismiss();
            Toast.makeText(context,"취소",Toast.LENGTH_SHORT)
        }
    }

    fun onWriteDBPost() {
        db = Firebase.database.reference
        var user = FirebaseAuth.getInstance().currentUser
        var cnt : Any =3
        level = dhEdHl.text.toString()
        diary = dhEdDiary.text.toString()
        //val myRef = database.getReference("posts")
        //val myRef = database.getReference(user?.uid.toString())
        Log.d("Han", "$cnt")
        val postValues: HashMap<String, Any> = HashMap()
        val postCounts: HashMap<String, Any> = HashMap()
       // postValues["date"] = formatted
        val myRefCount = db.child(user!!.uid).child("count").child(date)
        postCounts["count"] = 2          //카운트 조건 추가
        myRefCount.setValue(postCounts)
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cnt = snapshot.child("count").value!!
                var cnt1 = snapshot.childrenCount

                Log.d("Han", "$cnt")
                val myRefDiary = db.child(user!!.uid).child("diary").child(year).child(monthformatted).child(dayformatted).child(cnt.toString())

                postValues["level"] = level
                postValues["diary"] = diary
                myRefDiary.setValue(postValues)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        db.child(user!!.uid).child("count").child(date).addValueEventListener(postListener)




        Toast.makeText(context,"저장 완료",Toast.LENGTH_SHORT)
    }
    /*
    fun readID(uid:String): String {
        db = Firebase.database.reference
        var Did: String="1"
        db.child(uid).child(formatterYear.toString()).child(formatterMonth.toString()).runTransaction(object : Transaction.Handler {
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
*/


}