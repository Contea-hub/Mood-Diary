package kr.ac.kpu.dailystone

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_lock.*

class AppPasswordActivity : AppCompatActivity() {

    private var oldPwd = ""
    private var changePwdUnlock = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock)

        val btnArray = arrayListOf<Button>(
            btn00,
            btn01,
            btn02,
            btn03,
            btn04,
            btn05,
            btn06,
            btn07,
            btn08,
            btn09,
            btnClear,
            btnErase
        )

        for (button in btnArray) {
            button.setOnClickListener(btnListener)
        }
    }

    private val btnListener = View.OnClickListener { view ->
        var currentValue = -1
        when (view.id) {
            R.id.btn00 -> currentValue = 0
            R.id.btn01 -> currentValue = 1
            R.id.btn02 -> currentValue = 2
            R.id.btn03 -> currentValue = 3
            R.id.btn04 -> currentValue = 4
            R.id.btn05 -> currentValue = 5
            R.id.btn06 -> currentValue = 6
            R.id.btn07 -> currentValue = 7
            R.id.btn08 -> currentValue = 8
            R.id.btn09 -> currentValue = 9
            R.id.btnClear -> onClear()
            R.id.btnErase -> onDeleteKey()
        }

        val strCurrentValue = currentValue.toString()   // 현재 입력된 번호 String으로 변경

        if (currentValue != -1) {
            when {
                etPasscode1.isFocused -> {
                    setEditText(etPasscode1, etPasscode2, strCurrentValue)
                }
                etPasscode2.isFocused -> {
                    setEditText(etPasscode2, etPasscode3, strCurrentValue)
                }
                etPasscode3.isFocused -> {
                    setEditText(etPasscode3, etPasscode4, strCurrentValue)
                }
                etPasscode4.isFocused -> {
                    etPasscode4.setText(strCurrentValue)
                }
            }
        }
        // 비밀번호 4자리 모두 입력 시
        if (etPasscode1.text.isNotEmpty() && etPasscode2.text.isNotEmpty() && etPasscode3.text.isNotEmpty() && etPasscode4.text.isNotEmpty()) {
            inputType(intent.getIntExtra("type", 0))
        }
    }

    //한 칸 지우기를 눌렀을 때
    private fun onDeleteKey() {
        when {
            etPasscode1.isFocused -> {
                etPasscode1.setText("")
            }
            etPasscode2.isFocused -> {
                etPasscode1.setText("")
                etPasscode1.requestFocus()
            }
            etPasscode3.isFocused -> {
                etPasscode2.setText("")
                etPasscode2.requestFocus()
            }
            etPasscode4.isFocused -> {
                etPasscode3.setText("")
                etPasscode3.requestFocus()
            }
        }
    }

    // 비밀번호 모두 지우기
    private fun onClear() {
        etPasscode1.setText("")
        etPasscode2.setText("")
        etPasscode3.setText("")
        etPasscode4.setText("")
        etPasscode1.requestFocus()
    }

    //입력된 비밀번호 하나로 합치기
    private fun inputedPassword(): String {
        return "${etPasscode1.text}${etPasscode2.text}${etPasscode3.text}${etPasscode4.text}"
    }

    //EditText 설정
    private fun setEditText(
        currentEditText: EditText,
        nextEditText: EditText,
        strCurrentValue: String
    ) {
        currentEditText.setText(strCurrentValue)
        nextEditText.requestFocus()
        nextEditText.setText("")
    }

    private fun inputType(type: Int) {
        when (type) {

            AppLockConst.ENABLE_PASSLOCK -> { // 2차 비밀번호 설정
                if (oldPwd.isEmpty()) {
                    oldPwd = inputedPassword()
                    onClear()
                    etInputInfo.text = "다시 한번 입력해주세요"
                } else {
                    if (oldPwd == inputedPassword()) {
                        AppLock(this).setPassLock(inputedPassword())
                        setResult(Activity.RESULT_OK)
                        finish()
                    } else {
                        onClear()
                        oldPwd = ""
                        etInputInfo.text = "비밀번호 입력"
                    }

                }
            }

            AppLockConst.DISABLE_PASSLOCK -> { // 2차비밀번호 삭제
                if (AppLock(this).isPassLockSet()) {
                    if (AppLock(this).checkPassLock((inputedPassword()))) {
                        AppLock(this).removePassLock()
                        setResult(Activity.RESULT_OK)
                        finish()
                    } else {
                        etInputInfo.text = "비밀번호가 틀립니다"
                        onClear()
                    }
                } else {
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }
            }

            AppLockConst.UNLOCK_PASSWORD ->
                if (AppLock(this).checkPassLock((inputedPassword()))) {
                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    etInputInfo.text = "비밀번호가 틀립니다"
                    onClear()
                }

            AppLockConst.CHANGE_PASSWORD -> {

                //if (oldPwd == inputedPassword() && !changePwdUnlock) {
                if (AppLock(this).checkPassLock(inputedPassword()) && !changePwdUnlock) {
                    onClear()
                    Toast.makeText(this,"2차 비밀번호 변경",Toast.LENGTH_SHORT).show()
                    changePwdUnlock = true
                    etInputInfo.text = "새로운 비밀번호 입력"
                } else if (changePwdUnlock) {
                    if (oldPwd.isEmpty()) {
                        oldPwd = inputedPassword()
                        onClear()
                        etInputInfo.text = "새로운 비밀번호 다시 입력"
                    } else {
                        if (oldPwd == inputedPassword()) {
                            AppLock(this).setPassLock(inputedPassword())
                            setResult(Activity.RESULT_OK)
                            finish()
                        } else {
                            onClear()
                            oldPwd = ""
                            etInputInfo.text = "현재 비밀번호 다시 입력"
                            changePwdUnlock = false
                        }
                    }
                } else {
                    etInputInfo.text = "비밀번호가 틀립니다"
                    changePwdUnlock = false
                    onClear()
                }
            }
        }
    }
}

