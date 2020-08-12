package kr.ac.kpu.dailystone

import android.view.View.inflate
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.inflate
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.dialog_goal.*
import java.util.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
class fDialogGoal(context : Context, date:String) /*date:String*/ : Dialog(context) {
    var user = FirebaseAuth.getInstance().currentUser
    var db= Firebase.database.reference
    private lateinit var goal : Any

    /*private val current: LocalDate = LocalDate.now()
    private var formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    private var formatted: String = current.format(formatter)
    var date = formatted.substring(2,8)*/
    var date = date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_goal)


        btnGoalApply.setOnClickListener {
            onDBSet()
            dismiss()
        }

        btnGoalCancel.setOnClickListener {
            dismiss()
        }
    }
    fun onDBSet(){

            val postValues: HashMap<String, Any> = HashMap()



            val postListener = object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child("count").child(date).child("goal").value == null) {
                        goal = 0
                    } else {
                        goal = etGoal.text.toString().toInt()
                    }
                    postValues["goal"] = goal
                    val myRefGoal = db.child(user!!.uid).child("count").child(date)
                    myRefGoal.setValue(postValues)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            }
            db.child(user!!.uid).addListenerForSingleValueEvent(postListener)
        }
}