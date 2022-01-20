package kst.app.newimentrance.data.repository

import kst.app.newimentrance.data.entity.LoginResponse

interface LoginRepository {

    // 로그인 네트워크 실행
    suspend fun getLoginResult(centerId: String, centerPw: String) : LoginResponse?

    // 쉐어드에 centerId, centerPw 저장
    suspend fun autoLoginSave(centerId: String, centerPw: String)

    // 자동로그인 할 Id,Pw가 있는지 검사
    suspend fun checkLoginSave() : Pair<String?, String?>
}