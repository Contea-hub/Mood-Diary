package kr.ac.kpu.dailystone

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_lock_choice.*

class LockActivity : AppCompatActivity() {

    var lock = true // 잠금 상태 여부 확인

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock_choice)
        init()
        //2차 비밀번호 설정 눌렀을 경우
        btnLock.setOnClickListener {
            val intent = Intent(this, AppPasswordActivity::class.java).apply {
                putExtra(AppLockConst.type, AppLockConst.ENABLE_PASSLOCK)
            }
            startActivityForResult(intent, AppLockConst.ENABLE_PASSLOCK)
            Log.d("Min", "success: start")
        }

        //2차 비밀번호 비활성화 눌렀을 경우
        btnDisable.setOnClickListener {
            val intent = Intent(this, AppPasswordActivity::class.java).apply {
                putExtra(AppLockConst.type, AppLockConst.DISABLE_PASSLOCK)
            }
            startActivityForResult(intent, AppLockConst.DISABLE_PASSLOCK)
        }

        //2차 비밀번호 변경 눌렀을 경우
        btnPassChange.setOnClickListener {
            val intent = Intent(this, AppPasswordActivity::class.java).apply {
                putExtra(AppLockConst.type, AppLockConst.CHANGE_PASSWORD)
            }
            startActivityForResult(intent, AppLockConst.CHANGE_PASSWORD)
        }
    }

    override fun onStop() {
        super.onStop()

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            AppLockConst.ENABLE_PASSLOCK ->
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
                }

            AppLockConst.UNLOCK_PASSWORD ->
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "잠금 해제 완료", Toast.LENGTH_SHORT).show()
                    lock = false
                }
        }
    }

   /* override fun onStart() {
        super.onStart()
        if (lock && AppLock(this).isPassLockSet()) {
            val intent = Intent(this, AppPasswordActivity::class.java).apply {
                putExtra(AppLockConst.type, AppLockConst.UNLOCK_PASSWORD)
            }
            startActivityForResult(intent, AppLockConst.UNLOCK_PASSWORD)
        }
    }*/

  /*  override fun onPause() {
        super.onPause()
        if (AppLock(this).isPassLockSet()) {
            lock = true
        }
    }*/

    private fun init() = if (AppLock(this).isPassLockSet()) {
        btnLock.isEnabled = false
        btnDisable.isEnabled = true
        btnPassChange.isEnabled = true
        lock = true
    } else {
        btnLock?.isEnabled = true
        btnDisable?.isEnabled = false
        btnPassChange?.isEnabled = false
        lock = false

    }
}