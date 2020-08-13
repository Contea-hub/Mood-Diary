package kr.ac.kpu.dailystone
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_lock_choice.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.activity_main.viewPager
import kotlinx.android.synthetic.main.fragment_main.*
import kr.ac.kpu.dailystone.AppLockConst.dbLock

class MainActivity : AppCompatActivity() {
    val adapter = MainAdapter(supportFragmentManager)
    var lock = true // 잠금 상태 여부 확인

    private lateinit var db: DatabaseReference

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = Firebase.database.reference
        var user = FirebaseAuth.getInstance().currentUser

        val lockListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child("lock").value == null){
                    //db.child(user!!.uid).child("password").child("lock").setValue(0)
                    //db.child(user!!.uid).child("password").child("password").setValue("")
                }else{
                    dbLock = snapshot.child("lock").value.toString().toInt()
                    Log.d("kkk","dbLock: $dbLock")
                    if (lock && AppLock(baseContext).isPassLockSet() && dbLock == 1) {
                        Log.d("kkk","if문 안의 dbLock: $dbLock")
                        val intent = Intent(baseContext, AppPasswordActivity::class.java).apply {
                            putExtra(AppLockConst.type, AppLockConst.UNLOCK_PASSWORD)
                        }
                        startActivityForResult(intent, AppLockConst.UNLOCK_PASSWORD)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        }
        db.child(user!!.uid).child("password")
            .addValueEventListener(lockListener)

        init()
        Log.d("kkk","함수 실행 후 dbLock: $dbLock")

        viewPager.adapter = adapter
        dots_indicator.setViewPager(viewPager)
        /*adapter.addFragment(MainFragment())
        adapter.addFragment(MonthFragment())
        adapter.addFragment(UserFragment())*/
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            /* AppLockConst.ENABLE_PASSLOCK ->
                 if (resultCode == Activity.RESULT_OK) {
                     Toast.makeText(this, "암호 설정 완료", Toast.LENGTH_SHORT).show()
                     init()
                     lock = false
                 }
             AppLockConst.DISABLE_PASSLOCK ->
                 if (resultCode == Activity.RESULT_OK) {
                     Toast.makeText(this, "암호 삭제 완료", Toast.LENGTH_SHORT).show()
                     init()
                 }
             AppLockConst.CHANGE_PASSWORD ->
                 if (resultCode == Activity.RESULT_OK) {
                     Toast.makeText(this, "암호 변경 완료", Toast.LENGTH_SHORT).show()
                     lock = false
                 }*/
            AppLockConst.UNLOCK_PASSWORD ->
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "잠금 해제 완료", Toast.LENGTH_SHORT).show()
                    lock = false
                }
        }
    }
    private fun readLock(){


    }

    override fun onPause() {
        super.onPause()
        if (AppLock(this).isPassLockSet()) {
            lock = true
        }
    }
    private fun init() = if (AppLock(this).isPassLockSet()) {
        lock = true
    } else {
        lock = false
    }
}