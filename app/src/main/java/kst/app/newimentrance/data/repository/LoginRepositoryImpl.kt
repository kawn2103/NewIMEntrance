package kst.app.newimentrance.data.repository

import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kst.app.newimentrance.data.api.ImarkerApi
import kst.app.newimentrance.data.entity.LoginResponse
import kst.app.newimentrance.data.preference.PreferenceManager

class LoginRepositoryImpl(
        //di로 주입될 파라미터들
        private val imarkerApi: ImarkerApi,
        private val preferenceManager: PreferenceManager,
        private val dispatcher: CoroutineDispatcher
) : LoginRepository {

    // 로그인 네트워크 실행
    // withContext(코루틴)은 비동기 작업을 동기로 처리하기 위해 사용되는 방식이다.
    // 마지막 줄에 있는 결과가 자동으로 리턴값으로 지정되어 반환한다.
    // 해당 문법이 실행 되는 동안은 메인쓰레드의 코루틴이 멈추기 때문에 비동기 실행이지만 동기처럼 동작함
    override suspend fun getLoginResult(centerId: String, centerPw: String): LoginResponse? =
            withContext(dispatcher){
                Log.d("gwan2103","getLoginResult")
                // Post API로 데이터를 전송하기 위해 데이터 세팅
                val map = hashMapOf<String,String>()
                map["center_id"] = centerId
                map["center_pw"] = centerPw
                Log.d("gwan2103","map >>>>> $map")
                // 네트워크 통신과 결과 받아오기
                val result = imarkerApi.login(map).body()
                Log.d("gwan2103","getLoginResult >>>>> $result")
                result
    }

    // 쉐어드에 centerId, centerPw 저장
    override suspend fun autoLoginSave(centerId: String, centerPw: String) = withContext(dispatcher){
        // 쉐어드에 데이터 저장
        preferenceManager.putAccount(centerId,centerPw)
    }

    // 자동로그인 할 Id,Pw가 있는지 검사
    override suspend fun checkLoginSave() : Pair<String?, String?> = withContext(dispatcher) {
        // 쉐어드에서 데이터 추출
        preferenceManager.getAccount()
    }
}