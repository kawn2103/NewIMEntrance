package kst.app.newimentrance.data.preference

interface PreferenceManager {
    // 자동 로그인을 위한 센터 ID,PW 쉐어드 저장
    fun putAccount(centerId: String, centerPw: String)
    // 자동 로그인을 위한 센터 ID,PW 쉐어드 호출
    fun getAccount(): Pair<String?,String?>
}