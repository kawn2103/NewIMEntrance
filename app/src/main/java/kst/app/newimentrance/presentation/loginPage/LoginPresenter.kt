package kst.app.newimentrance.presentation.loginPage

import android.util.Log
import kotlinx.coroutines.*
import kst.app.newimentrance.data.repository.LoginRepository

class LoginPresenter(
    private val view: LoginContract.View,
    private val loginRepository: LoginRepository
) : LoginContract.Presenter{

    // 코루틴 스코프 선언
    override val scope: CoroutineScope = MainScope()

    // 로그인 실행
    override fun requestLogin(centerId: String, centerPw: String) {
        when {
            // 아이디가 빈 값일 때
            centerId == "" -> {
                view.errorMessage("아이디가 비어있습니다.\n 아이디를 입력해 주세요.")
            }
            // 비밀번호가 빈 값일 때
            centerPw == "" -> {
                view.errorMessage("비밀번호가 비어있습니다.\n 비밀번호를 입력해 주세요.")
            }
            //아이디 비밀번호가 정상적으로 들어가 있을 때 로그인 실행
            else -> {
                view.errorMessage("")
                view.showLoadingIndicator("id는 $centerId 이고 pw는 $centerPw 이다.")
                scope.launch {
                    Log.d("gwan2103","scope.launch")
                    val result = loginRepository.getLoginResult(centerId,centerPw)
                    Log.d("gwan2103","result >>>> $result")
                    loginRepository.autoLoginSave(centerId,centerPw)
                    view.hideLoadingIndicator()
                    view.transAttendanceView(result?.centerKey.toString())
                }
            }
        }
    }

    // 화면이 생성되면 바로 실행
    override fun onViewCreated() {
        scope.launch {
            val centerAccount = loginRepository.checkLoginSave()
            if (centerAccount.first != "-1"){
                view.showLoadingIndicator("자동 로그인 중입니다. 잠시만 기다려 주세요")
                val result = centerAccount.first?.let { centerId ->
                             centerAccount.second?.let { centerPw ->
                                 loginRepository.getLoginResult(centerId, centerPw)
                             }
                }
                Log.d("gwan2103","result >>>> $result")
                view.hideLoadingIndicator()
                view.transAttendanceView(result?.centerKey.toString())
            }
        }
    }

    override fun onDestroyView() { }
}