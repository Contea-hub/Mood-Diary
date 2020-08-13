package kr.ac.kpu.dailystone

object AppLockConst {
    val type = "type"
    val ENABLE_PASSLOCK = 1     // 2차비밀번호 설정
    val DISABLE_PASSLOCK = 2    // 2차비밀번호 삭제
    val CHANGE_PASSWORD = 3     // 2차비밀번호 변경
    val UNLOCK_PASSWORD = 4     // 잠금 해제
    var dbPassword = ""
    var dbLock = 0
}