package kr.ac.kpu.dailystone

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_userinfo.*
import org.w3c.dom.Text
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class UserFragment : Fragment() {
    private var mAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    lateinit var database : DatabaseReference
    private val current: LocalDate = LocalDate.now()
    private var formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    private var formatted: String = current.format(formatter)
    var year = formatted.substring(2,4)
    var month = formatted.substring(4,6)
    companion object { // 상수 역할

        fun newInstance(): UserFragment {
            return UserFragment()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //mAuth = FirebaseAuth.getInstance()
        userBtninfo.setOnClickListener {
            val intent = Intent(context, UserInfoActivity::class.java)
            startActivity(intent)
        }

        userBtnReset.setOnClickListener {

        }

        userBtnLogout.setOnClickListener {      //클릭 시 로그아웃
            signOut()
        }

        btnLockSet.setOnClickListener {
            val intent = Intent(context, LockActivity::class.java)
            startActivity(intent)
        }
        btnGoalSet.setOnClickListener {
            val dlgView = layoutInflater.inflate(R.layout.dialog_goal_setting , null)
            val dlgBuilder = AlertDialog.Builder(context)
            val currentGoal = dlgView.findViewById<TextView>(R.id.tvCurrentGoal)
            val settingGoal = dlgView.findViewById<EditText>(R.id.etSettingGoal)
            val uid = mAuth?.uid
            var setGoal : String? = null
            settingGoal.hint = "(15 ~ 99)"
            database = Firebase.database.reference
            val postListener = object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    setGoal = snapshot.child("goal").value.toString()
                    currentGoal.text = setGoal
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            }
            database.child(uid!!).child("goal").child(year).child(month).addValueEventListener(postListener)

            dlgBuilder.setTitle("월별 목표치 설정")
            dlgBuilder.setView(dlgView)
            dlgBuilder.setPositiveButton("확인") {dialogInterface, i ->
                if(settingGoal.text.toString() == ""){
                    Toast.makeText(context, "목표치를 입력해주세요", Toast.LENGTH_SHORT).show()
                }else if(settingGoal.text.toString().toInt() < 14 || settingGoal.text.toString().toInt() > 100) {
                    Toast.makeText(context, "범위에 맞게 입력해주세요", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context, "목표치 변경이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    database.child(uid!!).child("goal")
                        .child(year).child(month).child("goal").setValue(settingGoal.text.toString())
                }

            }.setNegativeButton("취소"){dialogInterface, i ->

            }.show()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_userinfo, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    private fun signOut(){
        val intent = Intent(context, LoginActivity::class.java)
        FirebaseAuth.getInstance().signOut()
        startActivity(intent)
        activity?.finish()
    }

}